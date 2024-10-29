let widget: SCWidget;
let queue: string[] = [];
let queueIndex: number = -1;

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
    widget.bind("ready", resume);
    widget.bind("finish", next);
    widget.bind("play", volume);
});

function resume() {
    metadata(queue[queueIndex]);
    volume();
    widget.play();
}

function volume() {
    const scaleVolume = (linearValue: number): number => {
        return Math.round(Math.pow((linearValue / 100), 2) * 200);
    };

    const volumeControl = document.getElementById('volume-control') as HTMLInputElement;
    const linearVolume = Number(volumeControl.value);
    const scaledVolume = scaleVolume(linearVolume);
    widget.setVolume(scaledVolume);
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
                queue.push(json[i]);
            }
            next();
        });
}

function getNextTrackId(): string | null {
    if (queue.length == 0) return null;
    queueIndex = (queueIndex + 1) % queue.length;
    return queue[queueIndex];
}

function next() {
    let id = getNextTrackId();
    if (id == null) return;
    widget.load("https://api.soundcloud.com/tracks/" + id);
    widget.bind("ready", resume);
    resume();
}

function metadata(id: string) {
    const uri = 'https://soundcloud-playlist-randomizer.hawolt.com/api/track/' + id;
    fetch(uri, {
        method: "POST"
    })
        .then(response => response.json())
        .then(json => {
            seek(json['waveform'], json['duration'])
        });
}

function seek(uri: string, duration: string) {
    fetch(uri)
        .then(response => response.json())
        .then(json => {
            let ms = parseFloat(duration) / 1800;
            let blanks = 0;
            let samples = json['samples'];
            for (let i = 0; i < samples.length; i++, blanks++) {
                if (samples[i] != 0) break
            }
            if (blanks == 0) return
            let total = Math.floor(ms * (blanks - 1));
            widget.seekTo(total)
        });
}