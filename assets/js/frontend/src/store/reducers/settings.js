// types
import { createSlice } from '@reduxjs/toolkit';
import { getSettings, updateSettings } from 'services/settings';

// initial state
const initialState = {
    user: {
        isLoading: true,
        data: {},
        error: {},
        settingsItem: 0,
    },
};

const settings = createSlice({
    name: 'settings',
    initialState,
    reducers: {
        changeSettingsItem: (state, action) => {
            state.user.settingsItem = action.payload;
        }
    },
    extraReducers: (builder) => {
        builder.addCase(getSettings.pending, (state, action) => {
            state.user.isLoading = true;
        }),
        builder.addCase(getSettings.fulfilled, (state, action) => {
            state.user.isLoading = false;
            if (action.payload.status){
                state.user.data = action.payload.data;
            }
            else{
                state.user.error = action.payload.error;
            }
        })
        builder.addCase(getSettings.rejected, (state, action) => {
            state.user.error = action.payload.error;
        })
    },
})

export default settings.reducer

export const { changeSettingsItem } = settings.actions;