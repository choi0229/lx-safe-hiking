<template>
  <div>
    <div id="cesiumContainer" style="width:80%; height: 80vh;"></div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';

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
            const filteredData = filterGeoJsonByPMNTN_NM(geoJsonData, ' ');
    
            displayGeoJson(viewer, filteredData);
    
            animateMarker(viewer, filteredData);
  
            loadMarkers(viewer,"/data/2023산악사고_인왕산2.geojson","/images/danger.png");
        }

        async function fetchGeoJson() {
          const response = await fetch('/data/new북한산.geojson');
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
          stroke: Cesium.Color.HOTPINK,
          fill: Cesium.Color.PINK.withAlpha(0.5),
          strokeWidth: 3,
          clampToGround: true,
      })).then((dataSource) => {
          viewer.flyTo(dataSource);
      });
    }

    function animateMarker(viewer, geoJsonData) {

      let coordinates = [];

      geoJsonData.features.forEach((feature)=>{
        feature.geometry.coordinates.forEach(line => {
          coordinates = coordinates.concat(line);
        });
      })

    const start = Cesium.JulianDate.now();
    const stop = Cesium.JulianDate.addSeconds(start, coordinates.length, new Cesium.JulianDate());

    const positionProperty = new Cesium.SampledPositionProperty();

    for (let i = 0; i < coordinates.length; i++) {
        const time = Cesium.JulianDate.addSeconds(start, i, new Cesium.JulianDate());
        const [longitude, latitude] = coordinates[i];
        const position = Cesium.Cartesian3.fromDegrees(longitude, latitude);
        positionProperty.addSample(time, position);
    }

    viewer.entities.add({
        position: positionProperty,
        point: {
            pixelSize: 10,
            color: Cesium.Color.WHITE,
            heightReference: Cesium.HeightReference.CLAMP_TO_GROUND,
        }
    });

    viewer.clock.startTime = start.clone();
    viewer.clock.stopTime = stop.clone();
    viewer.clock.currentTime = start.clone();
    viewer.clock.clockRange = Cesium.ClockRange.LOOP_STOP;
    viewer.clock.multiplier = 10;
    viewer.clock.shouldAnimate = true;
    viewer.timeline.zoomTo(start, stop);
}


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
</script>

<style scoped>
#cesiumContainer {
  width: 100%;
  height: 100vh;
}
</style>
