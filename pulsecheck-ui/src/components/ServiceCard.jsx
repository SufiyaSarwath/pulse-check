import React from 'react';
import StatusDot from './StatusDot';
import { deleteService } from '../api/services';

export default function ServiceCard({ service, isSelected, onClick, onDeleted }) {
    const handleDelete = async (e) => {
        e.stopPropagation();
        if (window.confirm(`Delete ${service.name}?`)) {
            try {
                await deleteService(service.id);
                onDeleted();
            } catch (err) {
                alert("Failed to delete: " + err.message);
            }
        }
    };

    return (
        <div onClick={onClick} className={`cursor-pointer p-4 rounded-xl border transition-all ${isSelected ? 'bg-gray-800 border-blue-500' : 'bg-gray-900 border-gray-800 hover:border-gray-600'} mb-3`}>
            <div className="flex justify-between items-start mb-2">
                <div className="flex items-center gap-3">
                    <StatusDot isUp={service.currentStatus} />
                    <div>
                        <h4 className="font-semibold text-gray-100">{service.name}</h4>
                        <p className="text-xs text-gray-400 truncate max-w-[150px]">{service.url}</p>
                    </div>
                </div>
                <button onClick={handleDelete} className="text-gray-500 hover:text-red-400 p-1">
                    ✕
                </button>
            </div>
            <div className="flex gap-4 mt-4 text-xs">
                <div className="flex flex-col">
                    <span className="text-gray-500">Uptime</span>
                    <span className={`font-semibold ${service.uptimePercentage > 90 ? 'text-green-400' : 'text-red-400'}`}>
                        {service.uptimePercentage}%
                    </span>
                </div>
                <div className="flex flex-col">
                    <span className="text-gray-500">Avg Time</span>
                    <span className="font-semibold text-gray-300">{service.averageResponseTimeMs}ms</span>
                </div>
            </div>
        </div>
    );
}
