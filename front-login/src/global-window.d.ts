export declare global {
    interface Window {
        envVars: {
            host: string
            port: string
            protocol: 'http' | 'https'
            wsPort: string
        };
    }
}
