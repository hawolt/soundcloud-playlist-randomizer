var cache: string[] = [];

let widget: SCWidget;

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('add')?.addEventListener('click', addSourceInput)
    document.getElementById('load')?.addEventListener('click', request)
    document.getElementById('next')?.addEventListener('click', next)

    const volumeControl = document.getElementById('volume-control') as HTMLInputElement;
    volumeControl.addEventListener('input', (event) => {
        volume();
    });

    const iframe = document.getElementById('embed');
    if (iframe == undefined) return
    widget = window.SC.Widget(iframe);
    widget.bind("finish", next);
    widget.bind("play", volume);
});

function volume() {
    const scaleVolume = (linearValue: number): number => {
        return Math.round(Math.pow((linearValue / 100), 2) * 200);
    };

    const volumeControl = document.getElementById('volume-control') as HTMLInputElement;
    const linearVolume = Number(volumeControl.value);
    const scaledVolume = scaleVolume(linearVolume);
    widget.setVolume(scaledVolume);

    console.log("vol: "+scaledVolume)
}

function addSourceInput() {
    const sources = document.getElementById('sources');

    const element = document.createElement('input');
    element.classList.add('source');
    element.placeholder = 'link';

    sources?.appendChild(element);
}

function request() {
    const array = document.getElementById('sources')?.children;
    const collection = [];
    if (array == undefined) return;
    for (const element of array) {
        collection.push((element as HTMLInputElement).value.split("?")[0]);
    }
    fetch("https://soundcloud-playlist-randomizer.hawolt.com/api/load", {
        method: "POST",
        body: JSON.stringify(collection)
    }).then(response => response.json())
        .then(json => {
            track(json['id']);
        });
}

function track(id: string) {
    setTimeout(() => {
        const uri = 'https://soundcloud-playlist-randomizer.hawolt.com/api/status/' + id;
        fetch(uri)
            .then(response => response.json())
            .then(json => {
                if (json['status']) {
                    load(id);
                } else {
                    track(id);
                }
            });
    }, 1000)
}

function load(id: string) {
    const uri = 'https://soundcloud-playlist-randomizer.hawolt.com/api/fetch/' + id;
    fetch(uri)
        .then(response => response.json())
        .then(json => {
            for (let i = 0; i < json.length; i++) {
                cache.push(json[i]);
            }
            next();
        });
}

function next() {
    if (cache.length == 0) return
    const id = cache.pop();
    widget.load("https://api.soundcloud.com/tracks/" + id, {
        auto_play: true
    });
    widget.play();
}