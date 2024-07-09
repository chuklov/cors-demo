// src/pages/Home.js
import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => (
    <div>
        <h1>Welcome to the Home Page</h1>
        <Link to="/users">
            <button>Users</button>
        </Link>
    </div>
);

export default Home;
