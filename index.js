var map = L.map('map').setView([48.692054, 6.184417], 13);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

var stationMarkers = [];
var eduMarkers = [];
var stationFetchInterval;

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
            const marker = L.marker([etabl.coordonnees.lat, etabl.coordonnees.lon], {
                icon: L.icon({
                    iconUrl: 'https://leafletjs.com/examples/custom-icons/leaf-green.png',
                    iconSize: [38, 95],
                    iconAnchor: [22, 94],
                    popupAnchor: [-3, -76],
                    shadowUrl: 'https://leafletjs.com/examples/custom-icons/leaf-shadow.png',
                    shadowSize: [50, 64],
                    shadowAnchor: [4, 62]
                })
            }).addTo(map);
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

function removeMarkers(markers) {
    markers.forEach(marker => {
        map.removeLayer(marker);
    });
}

async function fetchMeteo() {
    const meteoResponse = await fetch('https://www.infoclimat.fr/public-api/gfs/json?_ll=48.67103,6.15083&_auth=ARsDFFIsBCZRfFtsD3lSe1Q8ADUPeVRzBHgFZgtuAH1UMQNgUTNcPlU5VClSfVZkUn8AYVxmVW0Eb1I2WylSLgFgA25SNwRuUT1bPw83UnlUeAB9DzFUcwR4BWMLYwBhVCkDb1EzXCBVOFQoUmNWZlJnAH9cfFVsBGRSPVs1UjEBZwNkUjIEYVE6WyYPIFJjVGUAZg9mVD4EbwVhCzMAMFQzA2JRMlw5VThUKFJiVmtSZQBpXGtVbwRlUjVbKVIuARsDFFIsBCZRfFtsD3lSe1QyAD4PZA%3D%3D&_c=19f3aa7d766b6ba91191c8be71dd1ab2');
    const meteoData = await meteoResponse.json();
    return meteoData;
}

async function fetchRestaurant() {
    const responseRestaurant = await fetch('');
    const dataRestaurant = await responseRestaurant.json();
    
    dataRestaurant.forEach(restaurant => {
        
    })
}


function displayMeteoMenu(meteoData) {
    const meteoMenu = document.getElementById('meteoMenu');
    meteoMenu.innerHTML = '';
    for (const [heure, data] of Object.entries(meteoData)) {
        if (heure.match(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/)) { // Ensure it is a timestamp
            const heureDiv = document.createElement('div');
            heureDiv.innerHTML = `
                <b>${heure}</b><br>
                Température: ${data.temperature['2m']} K<br>
                Risque de neige: ${data.risque_neige}<br>
                Risque de pluie: ${data.pluie} mm<br>
                Iso Zero: ${data.iso_zero} m<br>
                Vent: ${data.vent_moyen['10m']} m/s, Rafales: ${data.vent_rafales['10m']} m/s
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
    // Enlever les marqueurs des stations de vélos
    removeMarkers(stationMarkers);

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

// Initial fetch and interval setup
stationFetchInterval = setInterval(fetchStations, 5000);
fetchStations();
