import React from 'react';
export default function StatusDot({ isUp }) {
    if (isUp === null || isUp === undefined) return <div className="h-3 w-3 rounded-full bg-gray-500"></div>;
    return isUp 
        ? <div className="h-3 w-3 rounded-full bg-green-500 animate-pulse shadow-[0_0_8px_#22c55e]"></div>
        : <div className="h-3 w-3 rounded-full bg-red-500 shadow-[0_0_8px_#ef4444]"></div>;
}
