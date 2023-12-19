import { createAsyncThunk } from "@reduxjs/toolkit";
import LocalStorage from "./localStorage";

const ls = new LocalStorage()

const getSettings = createAsyncThunk('settings/get', async (queryData, { getState }) => {
    let url = `${process.env.REACT_APP_BASE_URL}/settings/get/`;
    const response = await fetch(url, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `token ${JSON.parse(ls.get('authorization')).token}`
        },
        body: JSON.stringify(queryData)
    })
    return response?.json();
});

const updateSettings = createAsyncThunk('settings/edit', async (queryData, { getState }) => {
    let url = `${process.env.REACT_APP_BASE_URL}/settings/edit/`;
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `token ${JSON.parse(ls.get('authorization')).token}`
        },
        body: JSON.stringify(queryData)
    })
    return response?.json();
});

export { getSettings, updateSettings };