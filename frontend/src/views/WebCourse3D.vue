<template>
    <div class="course-preview-page">
      <button @click="goBack" class="back-button"><img src="/images/뒤로가기.png"  alt="돋보기 아이콘" width="30" height="30"></button>
      <section class="annual-analysis-section" ref="annualAnalysisRef">
        <h2 class="section-title"></h2>
        <div class="stats-container">
          <div class="card stat-item1">
            <p class="stat-desc">거리(Km)</p>
            <span class="count-up" ref="growthRate">{{ growthRate }}</span>
            <span class="stat-title1">KM</span>
          </div>
          <div class="card stat-item2">
            <p class="stat-desc">고도(m)</p>
            <span class="count-up" ref="countries">{{ countries }}</span>
            <p class="stat-title2">M</p>
          </div>
        </div>
      </section>
  
      <div id="cesiumContainer" style="width:100%; height: 100vh;"></div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  const goBack = () => router.back();
    
    onMounted(() => {
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/cesium@1.122.0/Build/Cesium/Cesium.js';
    
        script.onload = () => {
            initCesium();
        };
        document.head.appendChild(script);
    
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = 'https://cesium.com/downloads/cesiumjs/releases/1.95/Build/Cesium/Widgets/widgets.css';
        document.head.appendChild(link);
    
      async function initCesium() {
            Cesium.Ion.defaultAccessToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI5Nzg1OWEzOS0yMWU0LTQ1MTEtOGY5NC1hZjg0YmFhNzgxMDMiLCJpZCI6MjU2MDAxLCJpYXQiOjE3MzE4OTgxMzB9.oNztRJnt40X1wN0JAS90SaLqSFEZIdQBSE7jFqLeH3g';
    
            const viewer = new Cesium.Viewer('cesiumContainer', {
                terrainProvider: await Cesium.createWorldTerrainAsync(),
  
              creditContainer: document.createElement('div'),
              animation: false,
  
              navigationHelpButton: false,
              homeButton: false,
              sceneModePicker: false,
              baseLayerPicker: false,
              fullscreenButton: false,
              infoBox: false,
              geocoder: false
            });
    
  
            const geoJsonData = await fetchGeoJson();
            const filteredData = filterGeoJsonByPMNTN_NM(geoJsonData, '마루');
    
            displayGeoJson(viewer, filteredData);
    
            animateMarker(viewer, filteredData);
  
            loadMarkers(viewer,"/data/2023산악사고_인왕산2.geojson","/images/danger.png");
        }
    
        async function fetchGeoJson() {
          const response = await fetch('/data/new북한산2.geojson');
          const data = await response.json();
          return data;
        }
    
        function filterGeoJsonByPMNTN_NM(geoJsonData, searchKeyword) {
          return{
              ...geoJsonData,
              features: geoJsonData.features.filter(feature =>
                  feature.properties.PMNTN_NM.includes(searchKeyword)
              )
          }
        }
    
        function displayGeoJson(viewer, geoJsonData) {
          viewer.dataSources.add(Cesium.GeoJsonDataSource.load(geoJsonData, {
              stroke: Cesium.Color.fromCssColorString('#00ff00'),
              fill: Cesium.Color.fromCssColorString('#00ff00'),
              strokeWidth: 3,
              clampToGround: true,
          })).then((dataSource) => {
              viewer.flyTo(dataSource);
          });
        }
    
        function animateMarker(viewer, geoJsonData) {
    
          let coordinates = [];
    
        geoJsonData.features.forEach((feature) => {
            feature.geometry.coordinates.forEach((line) => {
                if (Array.isArray(line) && line.length === 2) {
                    coordinates.push(line);
                }
            });
        });
    
        const start = Cesium.JulianDate.now();
        const stop = Cesium.JulianDate.addSeconds(start, coordinates.length, new Cesium.JulianDate());
    
        const positionProperty = new Cesium.SampledPositionProperty();
    
        for (let i = 0; i < coordinates.length; i++) {
            const time = Cesium.JulianDate.addSeconds(start, i, new Cesium.JulianDate());
            const [longitude, latitude] = coordinates[i];
            const position = Cesium.Cartesian3.fromDegrees(longitude, latitude);
            positionProperty.addSample(time, position);
        }
    
        const entity = viewer.entities.add({
            position: positionProperty,
            point: {
                pixelSize: 20,
                color: Cesium.Color.WHITE,
                outlineColor: Cesium.Color.fromCssColorString('#ffa500'),
                outlineWidth: 3,
                heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
            }
        });
    
    viewer.clock.onTick.addEventListener(() => {
        const position = entity.position.getValue(viewer.clock.currentTime);
        if (position) {
            const transform = Cesium.Transforms.eastNorthUpToFixedFrame(position);
            
            viewer.camera.lookAtTransform(
                transform,
                new Cesium.Cartesian3(-700, -1800, 1400)
            );
    
            viewer.camera.setView({
                orientation: {
                    heading: Cesium.Math.toRadians(30),
                    pitch: Cesium.Math.toRadians(-25),
                    roll: 0
                }
            });
        }
    });
    
        viewer.clock.startTime = start.clone();
        viewer.clock.stopTime = stop.clone();
        viewer.clock.currentTime = start.clone();
        viewer.clock.clockRange = Cesium.ClockRange.LOOP_STOP;
        viewer.clock.multiplier = 20;
        viewer.clock.shouldAnimate = true;
        viewer.timeline.zoomTo(start, stop);
    }
  
    if (annualAnalysisRef.value) countUpObserver.observe(annualAnalysisRef.value);
    
    
    });
  
    async function loadMarkers(viewer, url, defaultImageSrc) {
    try {
        const response = await fetch(url);
        const markerData = await response.json();

        const positions = markerData.features.map(spot => {
            const lat = spot.geometry.coordinates[1];
            const lng = spot.geometry.coordinates[0];
            return Cesium.Cartographic.fromDegrees(lng, lat);
        });

        const updatedPositions = await Cesium.sampleTerrainMostDetailed(viewer.terrainProvider, positions);

        updatedPositions.forEach((position, index) => {
            const altitude = position.height + 1.0;
            const spot = markerData.features[index];

            let imageSrc = defaultImageSrc;
            switch (spot.properties.type) {
                case '실족':
                    imageSrc = '/images/strumble.png';
                    break;
                case '조난':
                    imageSrc = '/images/distress.png';
                    break;
                case '질환':
                    imageSrc = '/images/disease.png';
                    break;
                case '밧줄':
                    imageSrc = '/images/rope.png';
                    break;
                default:
                    imageSrc = defaultImageSrc;
            }

            const entity = viewer.entities.add({
                position: Cesium.Cartesian3.fromDegrees(
                    Cesium.Math.toDegrees(position.longitude),
                    Cesium.Math.toDegrees(position.latitude),
                    altitude
                ),
                billboard: {
                    image: imageSrc,
                    verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
                    width: 40,
                    height: 40,
                },
                label: {
                    text: spot.properties.MNTN_NM || '',
                    font: '14pt sans-serif',
                    outlineColor: Cesium.Color.BLACK,
                    outlineWidth: 2,
                    verticalOrigin: Cesium.VerticalOrigin.BOTTOM,
                    pixelOffset: new Cesium.Cartesian2(0, -20),
                },
                description: `<img src="${imageSrc}" alt="사진" style="width: 150px; height: 100px;"/><br><div style="padding:5px;">${spot.properties.MNTN_NM || ''}<br>${spot.properties.SAFE_SPOT2 || ''}</div>`,
            });

            viewer.selectedEntityChanged.addEventListener((selectedEntity) => {
                if (Cesium.defined(selectedEntity) && selectedEntity === entity) {
                    viewer.infoBox.viewModel.showInfo = true;
                    viewer.infoBox.viewModel.description = entity.description;
                }
            });
        });
    } catch (error) {
        console.error(`${url} 파일 로드 중 에러 발생:`, error);
    }
}

  
  
  const growthRate = ref(0);
  const countries = ref(0);
  const annualAnalysisRef = ref(null);
  let countStarted = false;
  
  const countUpObserver = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting && !countStarted) {
      countStarted = true;
      countUp(5.6, growthRate);
      countUp(655, countries);
    }
  }, { threshold: 0.5 });
  
  function countUp(target, ref) {
    let current = 0;
    const interval = setInterval(() => {
      if (current < target) {
        current += Math.ceil(target / 100);
        ref.value.innerText = current > target ? target : current;
      } else {
        clearInterval(interval);
      }
    }, 20);
  }
  
  
    </script>
  
  <style scoped>
  .course-preview-page {
    position: relative;
    display: flex;
    flex-direction: column;
    width: 100vw;
    height: 100vh;
    overflow: hidden;
  }
  
  .back-button {
    position: absolute;
    top: 20px;
    left: 20px;
    background-color: rgba(255, 255, 255);
    color: rgb(12, 12, 12);
    border: none;
    padding: 10px;
    border-radius: 50%;
    width: 50px;
    height: 50px;
    z-index: 10;
    cursor: pointer;
    box-shadow: 0 1px 6px rgba(0, 0, 0, 0.9);
  }
  
  .video-container {
    position: relative;
    width: 100%;
    height: 100%;
  }
  
  .video-player {
    width: 100%;
    height: 100vh;
    object-fit: cover;
  }
  
  .annual-analysis-section {
      position: fixed;
      bottom: 0;
      left: 0;
      margin-bottom: 3rem;
      color: #fff;
      z-index: 100;
      margin-left: 0.8rem;
  }
  
  .section-title {
      font-weight: 700;
      font-size: 30px;
    color: #ffffff;
    margin-bottom: 1rem;
  }
  
  .section-description {
      font-weight: 700;
      font-size: 14px;
    color: #565656;
  }
  
  .stats-container {
    display: flex;
    gap: 1rem;
  }
  
  .stat-item1 {
      width: 150px;
      height: 70px;
      font-weight: 700;
      font-size: 30px;
      margin-top: 0.5rem;
  }
  .stat-item2 {
      width: 170px;
      height: 70px;
      font-weight: 700;
      font-size: 30px;
      margin-top: 0.5rem;
  }
  
  .count-up {
      font-weight: 700;
      font-size: 35px;
    color: #272727;
      margin-top: -2.7rem;
      padding: 0.8rem;
  }
  
  
  .stat-desc {
      font-weight: 700;
      font-size: 14px;
      color: #565656;
      padding: 0.3rem;
      margin-left: 0.6rem;
  }
  
  .card{
      background-color: white;
      border-radius: 10px;
      box-shadow: 0 3px 20px rgba(0, 0, 0, 0.9);
  }
  
  .stat-title1 {
      position: fixed;
      bottom: 0;
      left: 0;
      font-weight: 700;
      font-size: 14px;
      color: #565656;
      margin-bottom: 3.7rem;
      margin-left: 6rem;
  
  }
  
  .stat-title2 {
      position: fixed;
      bottom: 0;
      right: 0;
      font-weight: 700;
      font-size: 14px;
      color: #565656;
      margin-bottom: 3.5rem;
      margin-right: 5rem;
  
  }
  </style>
  