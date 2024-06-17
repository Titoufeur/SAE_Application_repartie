var map = L.map('map').setView([48.692054, 6.184417], 13);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

var stationMarkers = [];
var eduMarkers = [];
var restoMarker = [];
var stationFetchInterval;
var incidentMarkers = [];
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
            stationMarkers.push(marker);
        }
    });
}

async function fetchEtablissementsSup() {
    const response = await fetch('https://data.enseignementsup-recherche.gouv.fr/api/explore/v2.1/catalog/datasets/fr-esr-implantations_etablissements_d_enseignement_superieur_publics/records?limit=50&refine=localisation%3A%22Alsace%20-%20Champagne-Ardenne%20-%20Lorraine%3ENancy-Metz%3EMeurthe-et-Moselle%3ENancy%22');
    const data = await response.json();

    data.results.forEach(etabl => {
        if (etabl.coordonnees) {
            const marker = L.marker([etabl.coordonnees.lat, etabl.coordonnees.lon]).addTo(map);
            marker.bindPopup(`
                <b>${etabl.siege_lib}</b><br>
                Type: ${etabl.type_d_etablissement}<br>
                Nom: ${etabl.implantation_lib}<br>
                Effectif: ${etabl.effectif}<br>
                Adresse: ${etabl.adresse_uai}, ${etabl.code_postal_uai} ${etabl.localite_acheminement_uai}
            `);
            eduMarkers.push(marker);
        }
    });
}

async function fetchIncidents() {
    try {
        const response = await fetch('http://localhost:50000/incidents');
        const data = await response.json();

        data.incidents.forEach(incident => {
            const coords = incident.location.polyline.split(' ');
            const marker = L.marker([coords[0], coords[1]]).addTo(map);
            const startTime = new Date(incident.starttime);
            const date = startTime.toLocaleDateString();
            const time = startTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

            marker.bindPopup(`
                <b>${incident.short_description}</b><br>
                Type: ${incident.type}<br>
                Description: ${incident.description}<br>
                Date: ${date}<br>
                Heure: ${time}
            `);
            incidentMarkers.push(marker);
        });
    } catch (error) {
        console.error('Erreur lors de la récupération des incidents :', error);
    }
}

function removeMarkers() {
    stationMarkers.forEach(marker => {
        map.removeLayer(marker);
    });
    eduMarkers.forEach(marker => {
        map.removeLayer(marker);
    });
    restoMarker.forEach(marker => {
        map.removeLayer(marker);
    });
}

async function fetchMeteo() {
    const meteoResponse = await fetch('https://www.infoclimat.fr/public-api/gfs/json?_ll=48.67103,6.15083&_auth=ARsDFFIsBCZRfFtsD3lSe1Q8ADUPeVRzBHgFZgtuAH1UMQNgUTNcPlU5VClSfVZkUn8AYVxmVW0Eb1I2WylSLgFgA25SNwRuUT1bPw83UnlUeAB9DzFUcwR4BWMLYwBhVCkDb1EzXCBVOFQoUmNWZlJnAH9cfFVsBGRSPVs1UjEBZwNkUjIEYVE6WyYPIFJjVGUAZg9mVD4EbwVhCzMAMFQzA2JRMlw5VThUKFJiVmtSZQBpXGtVbwRlUjVbKVIuARsDFFIsBCZRfFtsD3lSe1QyAD4PZA%3D%3D&_c=19f3aa7d766b6ba91191c8be71dd1ab2');
    const meteoData = await meteoResponse.json();
    return meteoData;
}

async function fetchRestaurant() {
    try {
        const responseRestaurant = await fetch('http://localhost:50000/restaurants', {
            mode: 'cors' // Assurez-vous que le mode CORS est activé
        });
        const dataRestaurant = await responseRestaurant.json();

        dataRestaurant.forEach(restaurant => {
            if (restaurant.gpsCoordinates) {
                const [lat, lon] = restaurant.gpsCoordinates.split(',').map(coord => parseFloat(coord.trim()));
                const marker = L.marker([lat, lon]).addTo(map);
                marker.bindPopup(`
                    <b>${restaurant.name}</b><br>
                    Adresse: ${restaurant.address}<br>
                `);
                restoMarker.push(marker);
            }
        });
    } catch (error) {
        console.error('Erreur lors de la récupération des restaurants :', error);
    }
}



function displayMeteoMenu(meteoData) {
    const meteoMenu = document.getElementById('meteoMenu');
    meteoMenu.innerHTML = '';
    
    for (const [heure, data] of Object.entries(meteoData)) {
        if (heure.match(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/)) { // Ensure it is a timestamp
            const heureDiv = document.createElement('div');
            heureDiv.classList.add('meteo-item'); // Ajoute une classe pour styliser avec CSS
            heureDiv.innerHTML = `
                <div class="heure">${heure}</div>
                <span>Température: ${data.temperature['2m']} K</span><br>
                <span>Risque de neige: ${data.risque_neige}</span><br>
                <span>Risque de pluie: ${data.pluie} mm</span><br>
                <span>Iso Zero: ${data.iso_zero} m</span><br>
                <span>Vent: ${data.vent_moyen['10m']} m/s, Rafales: ${data.vent_rafales['10m']} m/s</span>
            `;
            meteoMenu.appendChild(heureDiv);
        }
    }
}



function hideMeteoMenu() {
    const meteoMenu = document.getElementById('meteoMenu');
    meteoMenu.style.display = 'none';
}

document.getElementById('stationVeloBoutton').addEventListener('click', async () => {
    // Enlever les marqueurs des établissements supérieurs
    removeMarkers(eduMarkers);

    // Cacher le menu de la météo
    hideMeteoMenu();

    // Redémarrer l'intervalle de mise à jour des stations de vélos
    if (!stationFetchInterval) {
        stationFetchInterval = setInterval(fetchStations, 5000);
    }

    // Réafficher les stations de vélos
    if (stationMarkers.length === 0) {
        await fetchStations();
    } else {
        stationMarkers.forEach(marker => marker.addTo(map));
    }
});

document.getElementById('menuBoutton').addEventListener('click', async () => {
    const meteoMenu = document.getElementById('meteoMenu');
    if (meteoMenu.style.display === 'none' || meteoMenu.style.display === '') {
        const meteoData = await fetchMeteo();
        displayMeteoMenu(meteoData);
        meteoMenu.style.display = 'block';
    } else {
        meteoMenu.style.display = 'none';
    }
});

document.getElementById('educationButton').addEventListener('click', async () => {
    // Enlever les marqueurs
    removeMarkers();

    // Enlever le fetch des stations de vélos de l'intervalle
    clearInterval(stationFetchInterval);

    // Cacher le menu de la météo
    hideMeteoMenu();

    // Afficher les établissements supérieurs
    if (eduMarkers.length === 0) {
        await fetchEtablissementsSup();
    } else {
        eduMarkers.forEach(marker => marker.addTo(map));
    }
});

document.getElementById('incidentsButton').addEventListener('click', async () => {

    removeMarkers(eduMarkers);
    clearInterval(stationFetchInterval);
    hideMeteoMenu();
    await fetchIncidents();
});


document.getElementById('restaurantButton').addEventListener('click', async () => {
    console.log("click on restaurant");
    removeMarkers();
    clearInterval(stationFetchInterval);
    hideMeteoMenu();
    if (restoMarker.length === 0) {
        await fetchRestaurant();
    } else {
        restoMarker.forEach(marker => marker.addTo(map));
    }
});

// Initial fetch and interval setup
stationFetchInterval = setInterval(fetchStations, 5000);
fetchStations();
