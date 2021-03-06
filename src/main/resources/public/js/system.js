
function getPlural(value, text) {
    value = parseInt(value);
    if (value === 0) {
        return "";
    } else if (value === 1) {
        return value + " " + text + " ";
    } else {
        return value + " " + text + "s ";
    }
}

function getDistance(km) {
    let formatter = new Intl.NumberFormat('en-GB', {
    });
    let result = km;
    let MILLION = 1000000;

    if (km < 10 * MILLION) {
        result = formatter.format(km) + " km";
    } else if (km < 1000 * MILLION) {
        km = parseInt(km / MILLION);
        result = formatter.format(km) + " Mkm";
    } else {
        km = parseInt(km / MILLION);
        result = formatter.format(km) + " Mkm";
        let au = parseInt(km / 150);
        result += " (" + formatter.format(au) + " AU)";
    }

    return result;
}

function getPressure(pascals) {
    let STANDARD = 100000;

    if (pascals >= 10 * STANDARD) {
        return parseInt(pascals / STANDARD) + " x Std";
    } else if (pascals >= 0.05 * STANDARD) {
        return parseInt((pascals * 100) / STANDARD) + "% Std";
    } else {
        let pct = parseInt((pascals * 1000) / STANDARD) / 10.0;
        return pct + "% Std";
    }
}

function getPeriod(seconds) {
    let period = "";
    seconds = parseInt(seconds);
    let s = parseInt(seconds % 60);
    seconds /= 60;

    let m = parseInt(seconds % 60);
    seconds /= 60;

    let h = parseInt(seconds % 24);
    seconds /= 24;

    let d = parseInt(seconds % 365);
    let y = parseInt(seconds / 365);

    period += getPlural(y, "year");

    if (y < 30) {
        // Include days if less than 30 years.
        period += getPlural(d, "day");

        if (y === 0 && d < 30) {
            // Include hours if less than 30 days.
            period += getPlural(h, "hour");

            if (y === 0 && d < 7) {
                // Include minutes if less than 7 days.
                period += getPlural(m, "minute");

                if (y === 0 && d === 0 && h < 10) {
                    // Include seconds if less than 10 hours.
                    period += getPlural(s, "second");
                }
            }
        }
    }

    return period.trim();
}

function showPlanets(starId) {
    $.getJSON("/api/star/" + starId + "/planets", function(resp) {
        var $planetMenu = $("#planet-menu-"+starId);
        var $planetContent = $("#planet-content-"+starId);

        $.each(resp, function(p){
            let planetId = resp[p].id;
            let planetName = resp[p].name;

            if (resp[p].moonOf === 0) {
                $planetMenu.append(`<li class="nav-item"><a class="nav-link" data-toggle="tab" id="planet-${planetId}-tab" href="#planet-${planetId}">${planetName}</a></li>`);

                $planetContent.append(`<div class="tab-pane fade" id="planet-${planetId}"></div>`);

                showPlanet(starId, resp[p]);
            }
        });

    });
}

function showMap(id) {
    $(`#map-${id}`).html("");
    $(`#map-${id}`).append(`<img src="/api/planet/${id}/map" width="100%"/>`);
}

function showStretchedMap(id) {
    $(`#map-${id}`).html("");
    $(`#map-${id}`).append(`<img src="/api/planet/${id}/map?stretch=true" width="100%"/>`);
}

/**
 * Display a 3D spinning globe using this world map.
 * @param id        ID of the planet to be shown.
 * @param planet    Planet data.
 * @param hasClouds Whether the planet has clouds.
 */
function showGlobe(id, planet, cloudLayers) {
    $(`#map-${id}`).html("");

    let scene = new THREE.Scene();
    let camera = new THREE.PerspectiveCamera(75, 800 / 400, 0.1, 2000);
    let renderer = new THREE.WebGLRenderer();

    renderer.setSize(800, 400);

    let viewPort = document.getElementById(`map-${id}`);
    viewPort.appendChild(renderer.domElement);
    var textureLoader = new THREE.TextureLoader();

    var geometry   = new THREE.SphereGeometry(2.5, 32, 32);
    var mainTexture = textureLoader.load(`/api/planet/${id}/map?stretch=true&width=1024`);
    var heightTexture = textureLoader.load(`/api/planet/${id}/map?stretch=true&name=height&width=1024`);
    var deformTexture = textureLoader.load(`/api/planet/${id}/map?stretch=true&name=deform&width=1024`);
    var material = new THREE.MeshPhongMaterial({ map: mainTexture, bumpMap: heightTexture, bumpScale: 0.5,
        displacementMap: deformTexture, shininess: 0.0, specular: 0xaaaaaa });

    var world = new THREE.Mesh(geometry, material);

    let light = new THREE.AmbientLight(0x888888);
    scene.add(light);

    let starLight = new THREE.PointLight(0xffffff, 1, 1000, 2);
    starLight.position.set(0.5, 0, 100);
    //starLight.position.multiplyScalar(3);
    starLight.castShadow = false;
    scene.add(starLight);

    scene.add(world);
    let clouds = [];

    if (cloudLayers) {
        let radius = 2.5;

        for (let layer=0; layer <= cloudLayers; layer++) {
            radius += 0.05;
            let cloudGeometry = new THREE.SphereGeometry(radius, 32, 32);
            let cloudTexture = textureLoader.load(`/api/planet/${id}/map?name=cloud-${layer}&width=1024`);

            let cloudMaterial = new THREE.MeshPhongMaterial({ map: cloudTexture, transparent: true });
            let cloudLayer = new THREE.Mesh(cloudGeometry, cloudMaterial);

            clouds.push(cloudLayer);
            scene.add(cloudLayer);
        }

    }

    camera.position.z = 5;

    var cloudRotation = 0;
    var pauseRotation = 0;
    viewPort.addEventListener('mousemove', function(event) {
        var mouse = { x: 0, y: 0};
        mouse.x = (event.clientX - renderer.domElement.offsetLeft) / renderer.domElement.width - 0.5;
        mouse.y = (event.clientY - renderer.domElement.offsetTop) / renderer.domElement.height - 0.5;

        world.rotation.x = Math.PI * 2 * mouse.y;
        world.rotation.y = Math.PI * 2 * mouse.x;

        pauseRotation = 200;
    }, false);

    let animate = function () {
        requestAnimationFrame( animate );

        if (pauseRotation) {
            pauseRotation--;

            if (clouds.length > 0) {
                for (let l=0; l < clouds.length; l++) {
                    clouds[l].material.opacity = (1 - (pauseRotation / 200));
                }
            }

        } else {
            world.rotation.y += 0.001;
        }
        if (clouds.length > 0) {
            cloudRotation += 0.001;
            if (cloudRotation > 2 * Math.PI) {
                cloudRotation -= 2 * Math.PI;
            }
            for (let l=0; l < clouds.length; l++) {
                clouds[l].rotation.x = world.rotation.x;
                clouds[l].rotation.y = world.rotation.y + (cloudRotation * (l+1));
            }
        }

        renderer.render(scene, camera);
    };
    animate();

}

function showPlanet(starId, planet) {
    let $planetInfo = $('#planet-' + planet.id);

    $planetInfo.html("");
    $planetInfo.append(`<div id="outermap-${planet.id}"><div id='map-${planet.id}' class='planet-map'></div></div>`);
    $planetInfo.append(`<dl id='planet-data-${planet.id}' class='data-block'></dl>`);

    $.getJSON(`/api/planet/${planet.id}/maps`, function(mapList) {
        let hasMain = false;
        let cloudLayers = 0;

        for (let i=0; i < mapList.length; i++) {
            if (mapList[i] === "main") {
                hasMain = true;
            } else if (mapList[i].startsWith("cloud-")) {
                // A planet can have multiple cloud layers.
                cloudLayers++;
            }
        }
        if (hasMain) {
            $(`#map-${planet.id}`).before(`<div id='ctrls-${planet.id}' class='btn-group planet-controls' data-toggle='buttons'></div>`);

            $(`#ctrls-${planet.id}`).append(`<label class="btn btn-secondary btn.sm active" ><input type="radio" value="map" checked/>Map</label>`);
            $(`#ctrls-${planet.id}`).append(`<label class="btn btn-secondary btn.sm"><input type="radio" value="globe">Globe</label>`);
            $(`#ctrls-${planet.id}`).append(`<label class="btn btn-secondary btn.sm"><input type="radio" value="stretch">Stretched</label>`);

            $(`#ctrls-${planet.id} label`).click(function() {
                $(this).addClass('active').siblings().removeClass('active');

                let type = $(this).find("input").val();
                let id = ("" + $(this).parent().attr("id")).replace(/\D/g, '');

                if (type === "map") {
                    showMap(id);
                } else if (type === "stretch") {
                    showStretchedMap(id);
                } else if (type === "globe") {
                    showGlobe(id, planet, cloudLayers);
                }
            });
            showMap(planet.id);
        }
    });

    let $planetData = $('#planet-data-'+planet.id);

    $planetData.append(`<dt>Type</dt><dd>${planet.type}</dd>`);
    $planetData.append(`<dt>Distance</dt><dd>${getDistance(planet.distance)}</dd>`);
    $planetData.append(`<dt>Year</dt><dd>${getPeriod(planet.period)}</dd>`);
    $planetData.append(`<dt>Radius</dt><dd>${getDistance(planet.radius)}</dd>`);
    if (planet.dayLength > 0) {
        $planetData.append(`<dt>Length of Day</dt><dd>${getPeriod(planet.dayLength)}</dd>`);
    }
    $planetData.append(`<dt>Atmosphere</dt><dd>${planet.pressure === 0?"":getPressure(planet.pressure)} ${planet.atmosphere}</dd>`);
    $planetData.append(`<dt>Temperature</dt><dd>${planet.temperature} K (${planet.temperature - 273} °C)</dd>`);

    if (planet.population > 0) {
        $planetData.append(`<dt>Population</dt><dd>${planet.population}</dd>`);
        $planetData.append(`<dt>Government</dt><dd>${planet.government} (${planet.law})</dd>`);
        $planetData.append(`<dt>Star Port</dt><dd>${planet.port}</dd>`);
        $planetData.append(`<dt>Tech Level</dt><dd>${planet.techLevel}</dd>`);
    }

    $planetInfo.append(`<p style="clear:left">${planet.description}</p>`);

    $planetInfo.append(`<div id='resources-list-${planet.id}' class='planet-resources'></div>`);
    var $planetResources = $(`#resources-list-${planet.id}`);

    $planetResources.append(`<ul id="resources-${planet.id}" class="resource-list"></ul>`);
    var $list = $(`#resources-${planet.id}`);
    $.each(planet.resources, function(r) {
        let resource = planet.resources[r];
        let density = resource.density / 10.0;
        if (density >= 100) {
            density = density.toPrecision(3);
        } else {
            density = density.toPrecision(2);
        }
        $list.append(`<li><img src="/icons/${resource.commodity.image}.png" title="${resource.commodity.name}" 
                width="64px"/><br/><span>${density}%</span></li>`)
    });

    $planetInfo.append("<div style='clear:both'></div>");
}
