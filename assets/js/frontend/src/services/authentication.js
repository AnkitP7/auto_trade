import { createAsyncThunk } from "@reduxjs/toolkit";

const login = createAsyncThunk('login', async (queryData) => {
    let url = `${process.env.REACT_APP_BASE_URL}/internal/login/`;
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(queryData)
    })
    return response?.json();
});


export { login };