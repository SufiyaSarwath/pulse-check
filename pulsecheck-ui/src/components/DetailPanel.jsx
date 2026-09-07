import React, { useEffect, useState } from 'react';
import { getChecks, getStats } from '../api/services';
import { timeAgo } from '../utils/timeAgo';
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, Cell } from 'recharts';

export default function DetailPanel({ serviceId }) {
    const [checks, setChecks] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        if (!serviceId) return;
        let mounted = true;
        const loadData = async () => {
            setLoading(true);
            try {
                const [checksData, statsData] = await Promise.all([ getChecks(serviceId), getStats(serviceId) ]);
                if (mounted) {
                    setChecks(checksData.reverse()); // Oldest first for chart left-to-right
                    setStats(statsData);
                    setError('');
                }
            } catch (err) {
                if (mounted) setError(err.message);
            } finally {
                if (mounted) setLoading(false);
            }
        };
        loadData();
        const interval = setInterval(loadData, 15000);
        return () => { mounted = false; clearInterval(interval); };
    }, [serviceId]);

    if (!serviceId) return <div className="flex h-full items-center justify-center text-gray-500 bg-gray-900 rounded-2xl border border-gray-800">Select a service to view details</div>;
    if (loading && !stats) return <div className="p-8 text-gray-400">Loading details...</div>;
    if (error) return <div className="p-8 text-red-400">Error: {error}</div>;

    return (
        <div className="bg-gray-900 rounded-2xl border border-gray-800 p-6 h-full overflow-y-auto">
            <div className="grid grid-cols-3 gap-4 mb-8">
                <div className="bg-gray-800 p-4 rounded-xl">
                    <p className="text-gray-400 text-xs uppercase font-bold mb-1">Uptime (24h)</p>
                    <p className={`text-3xl font-bold ${stats?.uptimePercentage > 90 ? 'text-green-400' : 'text-red-400'}`}>{stats?.uptimePercentage}%</p>
                </div>
                <div className="bg-gray-800 p-4 rounded-xl">
                    <p className="text-gray-400 text-xs uppercase font-bold mb-1">Avg Response</p>
                    <p className="text-3xl font-bold text-gray-100">{stats?.averageResponseTimeMs} <span className="text-lg text-gray-500">ms</span></p>
                </div>
                <div className="bg-gray-800 p-4 rounded-xl">
                    <p className="text-gray-400 text-xs uppercase font-bold mb-1">Total Checks</p>
                    <p className="text-3xl font-bold text-gray-100">{stats?.totalChecks}</p>
                </div>
            </div>

            <div className="mb-8">
                <h3 className="text-lg font-semibold mb-4 text-gray-200">Response Times</h3>
                <div className="h-48 w-full">
                    <ResponsiveContainer width="100%" height="100%">
                        <BarChart data={checks}>
                            <XAxis dataKey="checkedAt" hide />
                            <YAxis unit="ms" tick={{ fill: '#6b7280', fontSize: 12 }} stroke="#374151" />
                            <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: 'none', borderRadius: '8px', color: '#f3f4f6' }} 
                                     labelFormatter={() => ''} />
                            <Bar dataKey="responseTimeMs" radius={[4, 4, 0, 0]}>
                                {checks.map((entry, index) => (
                                    <Cell key={index} fill={entry.isUp ? '#22c55e' : '#ef4444'} />
                                ))}
                            </Bar>
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            </div>

            <div>
                <h3 className="text-lg font-semibold mb-4 text-gray-200">Recent Checks</h3>
                <table className="w-full text-left text-sm text-gray-400">
                    <thead className="bg-gray-800 text-xs uppercase">
                        <tr>
                            <th className="px-4 py-3 rounded-tl-lg">Time</th>
                            <th className="px-4 py-3">Status</th>
                            <th className="px-4 py-3">Code</th>
                            <th className="px-4 py-3 rounded-tr-lg">Response</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-800">
                        {[...checks].reverse().map(check => (
                            <tr key={check.id}>
                                <td className="px-4 py-3">{timeAgo(check.checkedAt)}</td>
                                <td className="px-4 py-3">
                                    <span className={`px-2 py-1 rounded-full text-xs font-semibold ${check.isUp ? 'bg-green-900/30 text-green-400' : 'bg-red-900/30 text-red-400'}`}>
                                        {check.isUp ? 'UP' : 'DOWN'}
                                    </span>
                                </td>
                                <td className="px-4 py-3">{check.statusCode || '-'}</td>
                                <td className="px-4 py-3">{check.responseTimeMs} ms</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
