import React, { useState } from 'react';

const SearchBar = ({ onSearchButtonClick, onSubcategorySelect, selectedSubcategory }) => {
    const [searchTerm, setSearchTerm] = useState('');

    const handleSearchInputChange = (e) => {
        setSearchTerm(e.target.value);
    };

    return (
        <div className="flex items-center space-x-2 mb-4">
            <input
                type="text"
                placeholder="Search products..."
                value={searchTerm}
                onChange={handleSearchInputChange}
                className="border p-2 rounded-md"
            />
            <button
                type="button"
                onClick={() => onSearchButtonClick(searchTerm)}
                className="bg-blue-500 text-white px-4 py-2 rounded-md"
            >
                Search
            </button>

            <div className="flex items-center ml-4">
                <select
                    value={selectedSubcategory}
                    onChange={onSubcategorySelect}
                    className="border p-2 rounded-md"
                >
                    <option value="">All Subcategories</option>
                    <option value="Topwear">Tops</option>
                    <option value="BottomWear">Bottom</option>
                    <option value="Dress">Dresses</option>
                    <option value="Flip Flops">Flip Flop</option>
                    <option value="Shoes">Shoes</option>
                    <option value="Sandal">Sandal</option>
                </select>
            </div>
        </div>
    );
};

export default SearchBar;
