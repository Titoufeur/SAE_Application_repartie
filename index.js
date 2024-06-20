var map = L.map('map').setView([48.692054, 6.184417], 13);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

var stationMarkers = [];
var eduMarkers = [];
var restoMarker = [];
var stationFetchInterval;
var incidentMarkers = [];
const urlProxy = 'http://10.11.87.109:8080';


async function fetchUrl(url) {
    try {
        const response = await fetch(url);
        return await response.json();
    } catch (error) {
        console.log('Erreur lors d\'un fetch. Tentative de passer par le proxy. ' + error);
        try {
            const proxyResponse = await fetch(urlProxy + '/fetch?url=' + url, {
                mode: 'cors'
            });
            console.log('Fetch réalisé via le proxy');
            return await proxyResponse.json();
        } catch (proxyError) {
            console.log('Erreur lors du fetch par Proxy. Abandon.' + proxyError);
            return null;
        }
    }
}
async function fetchStations() {
    removeMarkers();
    console.log('FetchStations')
    const stationInfoData = await fetchUrl('https://transport.data.gouv.fr/gbfs/nancy/station_information.json');

    const stationStatusData = await fetchUrl('https://transport.data.gouv.fr/gbfs/nancy/station_status.json')

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
    const data = await fetchUrl('https://data.enseignementsup-recherche.gouv.fr/api/explore/v2.1/catalog/datasets/fr-esr-implantations_etablissements_d_enseignement_superieur_publics/records?limit=50&refine=localisation%3A%22Alsace%20-%20Champagne-Ardenne%20-%20Lorraine%3ENancy-Metz%3EMeurthe-et-Moselle%3ENancy%22')

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
        //const response = await fetch(urlProxy + '/incidents');
        const data = await fetchUrl('https://carto.g-ny.org/data/cifs/cifs_waze_v2.json');

        data.incidents.forEach(incident => {
            const coords = incident.location.polyline.split(' ');
            const address = incident.location.street;
            const marker = L.marker([coords[0], coords[1]]).addTo(map);
            const startTime = new Date(incident.starttime);
            const date = startTime.toLocaleDateString();
            const time = startTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

            marker.bindPopup(`
                <b>${incident.short_description}</b><br>
                Type: ${incident.type}<br><br>
                Description: ${incident.description}<br><br>
                Date: ${date}<br>
                Heure: ${time}<br>
                Lieux: ${address}
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
    incidentMarkers.forEach(marker => {
        map.removeLayer(marker);
    })
}

async function fetchRestaurant() {
    removeMarkers();
    try {
        const response = await fetch(urlProxy + '/restaurants', {
            mode: 'cors'
        });
        const restaurants = await response.json();

        restaurants.forEach(restaurant => {
            if (restaurant.gpsCoordinates) {
                const [lat, lon] = restaurant.gpsCoordinates.split(',').map(coord => parseFloat(coord.trim()));
                const marker = L.marker([lat, lon]).addTo(map);
                marker.restaurantInfo = restaurant;

                // On crée un élément dom pour ajouter la popup
                const popupContent = document.createElement('div');
                popupContent.innerHTML = `
            <b>${restaurant.name}</b><br>
            Adresse: ${restaurant.address}<br>
            <button class="reservation-button">Réserver</button>
        `;

                marker.bindPopup(popupContent);

                marker.on('popupopen', () => {
                    const popup = marker.getPopup();
                    const content = popup.getContent();

                    if (content instanceof HTMLElement) {
                        const reserveButton = content.querySelector('.reservation-button');
                        reserveButton.addEventListener('click', () => {
                            showReservationForm(restaurant);
                        });
                    } else {
                        console.error('Le contenu du popup n\'est pas un élément DOM :', content);
                    }
                });
                restoMarker.push(marker);
            }
        });

    } catch (error) {
        console.error('Erreur lors de la récupération des restaurants :', error);
    }
}

function showReservationForm(restaurant) {
    hideMenus();
    const formHtml = `
        <div id="reservation-form">
            <h3>Réserver pour ${restaurant.name}</h3>
            <form id="reservation-form-data">
                <label for="firstName">Prénom:</label><br>
                <input type="text" id="firstName" name="firstName"><br>
                <label for="lastName">Nom:</label><br>
                <input type="text" id="lastName" name="lastName"><br>
                <label for="numGuests">Nombre de convives:</label><br>
                <input type="number" id="numGuests" name="numGuests"><br>
                <label for="phone">Téléphone:</label><br>
                <input type="text" id="phone" name="phone"><br><br>
                <input type="submit" value="Réserver">
                <button type="button" id="btnCancel">Annuler</button>
            </form>
        </div>
    `;

    // Ajout du formulaire au DOM
    document.body.insertAdjacentHTML('beforeend', formHtml);
    //Bouton pour annuler et cacher le formulaire
    const btnCancel = document.getElementById("btnCancel");
    btnCancel.addEventListener("click", () => {
        hideMenus();
    });
    // Ajout d'un eventlistener sur le formulaire
    const reservationForm = document.getElementById('reservation-form-data');
    reservationForm.addEventListener('submit', async function(event) {
        event.preventDefault(); // pour empêcher le rechargement de la page

        const formData = new FormData(reservationForm);
        const firstName = formData.get('firstName');
        const lastName = formData.get('lastName');
        const numGuests = formData.get('numGuests');
        const phone = formData.get('phone');

        // on construit l'objet de réservation à partir des valeurs qu'on vient de récupérer
        const reservationData = {
            firstName: firstName,
            lastName: lastName,
            numGuests: parseInt(numGuests),
            phone: phone,
            restaurantId: restaurant.id
        };

        try {
            // on envoie la requête post pour réserver la table demandée par le formulaire
            const response = await fetch(urlProxy + '/restaurants', {
                mode: 'cors',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reservationData)
            });

            if (response.ok) {//Lorsqu'on fait la requête POST, on reçoit une réponse pour savoir si c'est bon ou pas.
                const result = await response.json();
                console.log('Réservation réussie:', result);
                alert('Réservation réussie ! 1 table pour ' + result.firstName + ' ' + result.lastName);
            } else {
                console.error('Erreur lors de la réservation:', response.status);
                alert('Erreur lors de la réservation. Veuillez réessayer.');
            }
        } catch (error) {
            console.error('Erreur inattendue lors de la réservation:', error);
        }
        finally {
            hideMenus();
        }
    });
}

function displayMeteoMenu(meteoData) {
    hideMenus();
    const meteoMenu = document.getElementById('meteoMenu');
    meteoMenu.innerHTML = '';

    const days = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
    const months = ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'];

    // URL des icônes
    const sunIconUrl = 'sun.png';
    const rainIconUrl = 'cloudy.png'; // Remplacez par l'URL de votre icône de nuage pluvieux

    for (const [heure, data] of Object.entries(meteoData)) {
        if (heure.match(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/)) {
            const date = new Date(heure);
            const dayName = days[date.getDay()];
            const dayNumber = date.getDate();
            const monthName = months[date.getMonth()];
            const year = date.getFullYear();
            const hour = date.getHours().toString().padStart(2, '0');
            const minute = date.getMinutes().toString().padStart(2, '0');

            const temperatureCelsius = (data.temperature['2m'] - 273.15).toFixed(2);

            const weatherIconUrl = data.pluie > 0 ? rainIconUrl : sunIconUrl;

            const heureDiv = document.createElement('div');
            heureDiv.classList.add('meteo-item');
            heureDiv.innerHTML = `
                <div class="heure">${dayName} ${dayNumber} ${monthName} ${year} ${hour}:${minute}</div>
                <img src="${weatherIconUrl}" alt="Weather icon" style="width: 24px; height: 24px;"><br>
                <span>Température: ${temperatureCelsius} °C</span><br>
                <span>Risque de neige: ${data.risque_neige}</span><br>
                <span>Risque de pluie: ${data.pluie} mm</span><br>
                <span>Iso Zero: ${data.iso_zero} m</span><br>
                <span>Vent: ${data.vent_moyen['10m']} m/s, Rafales: ${data.vent_rafales['10m']} m/s</span>
            `;
            meteoMenu.appendChild(heureDiv);
        }
    }
}


//Fonction pour cacher les différents éléments qui s'affichent.
function hideMenus(){
    const restaurantForm = document.getElementById('new-restaurant-form');
    if (restaurantForm) {
        restaurantForm.remove();
    }
    const reservationForm = document.getElementById('reservation-form');
    if (reservationForm) {
        reservationForm.remove();
    }
    const meteoMenu = document.getElementById('meteoMenu');
    meteoMenu.style.display = 'none';
}


document.getElementById('stationVeloBoutton').addEventListener('click', async () => {
    removeMarkers();
    hideMenus();
    if (!stationFetchInterval) {
        stationFetchInterval = setInterval(fetchStations, 5000);
    }

    if (stationMarkers.length === 0) {
        await fetchStations();
    } else {
        stationMarkers.forEach(marker => marker.addTo(map));
    }
});

document.getElementById('menuBoutton').addEventListener('click', async () => {
    const meteoMenu = document.getElementById('meteoMenu');
    if (meteoMenu.style.display === 'none' || meteoMenu.style.display === '') {
        const meteoData = await fetchUrl('https://www.infoclimat.fr/public-api/gfs/json?_ll=48.67103,6.15083&_auth=ARsDFFIsBCZRfFtsD3lSe1Q8ADUPeVRzBHgFZgtuAH1UMQNgUTNcPlU5VClSfVZkUn8AYVxmVW0Eb1I2WylSLgFgA25SNwRuUT1bPw83UnlUeAB9DzFUcwR4BWMLYwBhVCkDb1EzXCBVOFQoUmNWZlJnAH9cfFVsBGRSPVs1UjEBZwNkUjIEYVE6WyYPIFJjVGUAZg9mVD4EbwVhCzMAMFQzA2JRMlw5VThUKFJiVmtSZQBpXGtVbwRlUjVbKVIuARsDFFIsBCZRfFtsD3lSe1QyAD4PZA%3D%3D&_c=19f3aa7d766b6ba91191c8be71dd1ab2')
        displayMeteoMenu(meteoData);
        meteoMenu.style.display = 'block';
    } else {
        meteoMenu.style.display = 'none';
    }
});

document.getElementById('educationButton').addEventListener('click', async () => {
    removeMarkers();
    clearInterval(stationFetchInterval);
    hideMenus();

    if (eduMarkers.length === 0) {
        await fetchEtablissementsSup();
    } else {
        eduMarkers.forEach(marker => marker.addTo(map));
    }
});

document.getElementById('incidentsButton').addEventListener('click', async () => {

    removeMarkers(eduMarkers);
    clearInterval(stationFetchInterval);
    hideMenus();
    await fetchIncidents();
});


document.getElementById('restaurantButton').addEventListener('click', async () => {
    console.log("click on restaurant");
    removeMarkers();
    clearInterval(stationFetchInterval);
    hideMenus();
    if (restoMarker.length === 0) {
        await fetchRestaurant();
    } else {
        restoMarker.forEach(marker => marker.addTo(map));
    }
});

map.on('click', onMapClick);
function onMapClick(e) {
    hideMenus();
    const lat = e.latlng.lat;
    const lon = e.latlng.lng;

    const formHtml = `
        <div id="new-restaurant-form" style="position: absolute; top: 10px; left: 10px; background: white; padding: 10px; border: 1px solid black; z-index: 1000;">
            <h3>Ajouter un nouveau restaurant</h3>
            <form id="new-restaurant-form-data">
                <label for="restaurantName">Nom du restaurant:</label><br>
                <input type="text" id="restaurantName" name="restaurantName"><br>
                <label for="restaurantAddress">Adresse:</label><br>
                <input type="text" id="restaurantAddress" name="restaurantAddress"><br>
                <input type="hidden" id="restaurantLat" name="restaurantLat" value="${lat}">
                <input type="hidden" id="restaurantLon" name="restaurantLon" value="${lon}">
                <input type="submit" value="Ajouter">
                <button type="button" id="btnCancel">Annuler</button>
            </form>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', formHtml);
    //ajout d'un event listener sur le bouton pour fermer le formulaire d'ajout
    const btnCancel = document.getElementById("btnCancel");
    btnCancel.addEventListener("click", () => {
        hideMenus();
    });
    document.getElementById('new-restaurant-form-data').addEventListener('submit', async function(event) {
        event.preventDefault();

        const formData = new FormData(event.target);
        const restaurantName = formData.get('restaurantName');
        const restaurantAddress = formData.get('restaurantAddress');
        const restaurantLat = parseFloat(formData.get('restaurantLat'));
        const restaurantLon = parseFloat(formData.get('restaurantLon'));

        const newRestaurant = {
            name: restaurantName,
            address: restaurantAddress,
            gpsCoordinates: `${restaurantLat},${restaurantLon}`
        };

        try {
            const response = await fetch(urlProxy + '/restaurants', {
                mode: 'cors',
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newRestaurant)
            });

            if (response.ok) {
                fetchRestaurant();
                alert('Restaurant ajouté avec succès !');
            } else {
                console.error('Erreur lors de l\'ajout du restaurant:', response.status);
                alert('Erreur lors de l\'ajout du restaurant. Veuillez réessayer.');
            }
        } catch (error) {
            console.error('Erreur inattendue lors de l\'ajout du restaurant:', error);
            alert('Erreur inattendue. Veuillez réessayer.');
        } finally {
            hideMenus();
        }
    });
}
stationFetchInterval = setInterval(fetchStations, 10000);
fetchStations();
