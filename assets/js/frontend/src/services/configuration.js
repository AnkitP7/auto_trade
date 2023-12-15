import { createAsyncThunk } from "@reduxjs/toolkit";
import LocalStorage from "./localStorage";

const ls = new LocalStorage()

const getConfiguration = createAsyncThunk('configuration/list', async (queryData, { getState }) => {
    let url = `${process.env.REACT_APP_BASE_URL}/configuration/get/`;
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

const createConfiguration = createAsyncThunk('configuration/create', async (queryData, { getState }) => {
    let url = `${process.env.REACT_APP_BASE_URL}/configuration/create/`;
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

export { getConfiguration, createConfiguration };