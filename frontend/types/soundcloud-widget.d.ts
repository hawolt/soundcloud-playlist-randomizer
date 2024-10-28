declare global {
    interface SCWidget {
        load(urlOrId: string | number, options?: { auto_play?: boolean }): void;
        play(): void;
        pause(): void;
        stop(): void;
        seekTo(seconds: number): void;
        setVolume(volume: number): void;
        getPosition(callback: (position: number) => void): void;
        getDuration(callback: (duration: number) => void): void;
        getVolume(callback: (volume: number) => void): void;
        getCurrentSound(callback: (sound: { id: number; title: string; artwork_url?: string }) => void): void;
        bind(event: 'play' | 'pause' | 'finish' | 'ready', callback: () => void): void;
        unbind(event: 'play' | 'pause' | 'finish' | 'ready', callback: () => void): void;
    }

    interface Window {
        SC: {
            Widget: (element: HTMLElement) => SCWidget;
        };
    }
}

export {};
