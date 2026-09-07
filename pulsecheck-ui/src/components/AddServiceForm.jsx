import React, { useState } from 'react';
import { createService } from '../api/services';

export default function AddServiceForm({ onAdded }) {
    const [name, setName] = useState('');
    const [url, setUrl] = useState('');
    const [interval, setInterval] = useState(60);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            await createService({ name, url, intervalSeconds: interval });
            setName(''); setUrl(''); setInterval(60);
            onAdded();
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="bg-gray-900 p-4 rounded-xl border border-gray-800 mb-6">
            <h3 className="text-sm font-semibold mb-3 text-gray-300">Add New Service</h3>
            <div className="flex flex-col gap-3">
                <input required placeholder="Service Name (e.g. My API)" value={name} onChange={e => setName(e.target.value)} className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 outline-none" />
                <input required type="url" placeholder="https://..." value={url} onChange={e => setUrl(e.target.value)} className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 outline-none" />
                <div className="flex gap-3">
                    <select value={interval} onChange={e => setInterval(Number(e.target.value))} className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm flex-1 outline-none">
                        <option value={10}>Every 10s</option>
                        <option value={30}>Every 30s</option>
                        <option value={60}>Every 1m</option>
                    </select>
                    <button disabled={loading} className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors">
                        {loading ? 'Adding...' : 'Add'}
                    </button>
                </div>
            </div>
            {error && <p className="text-red-400 text-xs mt-2">{error}</p>}
        </form>
    );
}
