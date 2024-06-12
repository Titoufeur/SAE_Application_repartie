var map = L.map('map').setView([48.692054, 6.184417], 13);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

async function fetchStations() {
    const stationInfoResponse = await fetch('https://transport.data.gouv.fr/gbfs/nancy/station_information.json');
    const stationInfoData = await stationInfoResponse.json();

    const stationStatusResponse = await fetch('https://transport.data.gouv.fr/gbfs/nancy/station_status.json');
    const stationStatusData = await stationStatusResponse.json();

    const stations = stationInfoData.data.stations;
    const stationStatus = stationStatusData.data.stations;

    const statusMap = new Map();
    stationStatus.forEach(status => {
        statusMap.set(status.station_id, status);
    });

    stations.forEach(station => {
        const status = statusMap.get(station.station_id);
        if (status) {
            const marker = L.marker([station.lat, station.lon]).addTo(map);
            marker.bindPopup(`
                        <b>${station.name}</b><br>
                        ${station.address}<br>
                        Capacité: ${station.capacity}<br>
                        Vélos disponibles: ${status.num_bikes_available}<br>
                        Emplacements disponibles: ${status.num_docks_available}
                    `);
        }
    });
}

function fetchRestaurant(){
    fetch("http://localhost:8080/restaurants")
        .then(response => {
            if(response.ok){
                return response.json();
            } else {
                throw new Error("Erreur fetch api restaurant, response not ok");
            }
        }).then(data => {
            console.log(data);
        }).catch(error => {
            console.log("Erreur fetch api restaurant but response is ok");
        })
}

fetchRestaurant();
fetchStations();

setInterval(fetchStations, 5000);