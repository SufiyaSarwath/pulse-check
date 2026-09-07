import React, { useEffect, useState } from 'react';
import { getServices } from './api/services';
import AddServiceForm from './components/AddServiceForm';
import ServiceCard from './components/ServiceCard';
import DetailPanel from './components/DetailPanel';

function App() {
    const [services, setServices] = useState([]);
    const [selectedId, setSelectedId] = useState(null);
    const [error, setError] = useState('');

    const fetchServices = async () => {
        try {
            const data = await getServices();
            setServices(data);
            setError('');
        } catch (err) {
            setError('Could not reach backend. Retrying in 15s...');
        }
    };

    useEffect(() => {
        fetchServices();
        const interval = setInterval(fetchServices, 15000);
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="min-h-screen p-6 max-w-7xl mx-auto flex gap-6">
            <div className="w-1/3 flex flex-col h-[calc(100vh-3rem)]">
                <div className="mb-6">
                    <h1 className="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-green-400">PulseCheck</h1>
                    <p className="text-gray-500 text-sm">Service Uptime Monitor</p>
                </div>
                {error && <div className="bg-red-900/30 text-red-400 p-3 rounded-lg text-sm mb-4">{error}</div>}
                <AddServiceForm onAdded={fetchServices} />
                <div className="overflow-y-auto flex-1 pr-2">
                    {services.map(s => (
                        <ServiceCard 
                            key={s.id} 
                            service={s} 
                            isSelected={selectedId === s.id} 
                            onClick={() => setSelectedId(s.id)}
                            onDeleted={() => {
                                if (selectedId === s.id) setSelectedId(null);
                                fetchServices();
                            }}
                        />
                    ))}
                </div>
            </div>
            <div className="w-2/3 h-[calc(100vh-3rem)]">
                <DetailPanel serviceId={selectedId} />
            </div>
        </div>
    );
}
export default App;
