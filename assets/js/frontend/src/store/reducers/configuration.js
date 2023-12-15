// types
import { createSlice } from '@reduxjs/toolkit';
import { getConfiguration, createConfiguration } from 'services/configuration';

// initial state
const initialState = {
    list: {
        isLoading: false,
        data: [],
    },
    create: {
        isLoading: false,
        isVisible: false,
        data: {},
    },
};

const configuration = createSlice({
    name: 'configuration',
    initialState,
    reducers: {
        closeSnackBar: (state, action) => {
            state.error.open = action.payload.status;
        },
        drawerAction: (state, action) => {
            state.create = {
                ...state.create,
                isVisible: action.payload,
            }
        }
    },
    extraReducers: (builder) => {
        builder.addCase(getConfiguration.pending, (state, action) => {
            state.list.isLoading = true;
        })
        builder.addCase(getConfiguration.fulfilled, (state, action) => {
            state.list.isLoading = false;
            if (action.payload.status)
                state.list.data = action.payload.data;
            else
                state.error = { ...action.payload.error, open: true, message: action.payload.error.message, }
        })
        builder.addCase(getConfiguration.rejected, (state, action) => {
            state.error = action.payload;
        })
        builder.addCase(createConfiguration.pending, (state, action) => {
            state.create.isLoading = true;
        })
        builder.addCase(createConfiguration.fulfilled, (state, action) => {
            state.create.isLoading = false;
            if (action.payload.status)
                state.create.data = action.payload.data;
            else
                state.error = { ...action.payload.error, open: true, message: action.payload.error.message, }
        })
        builder.addCase(createConfiguration.rejected, (state, action) => {
            state.error = action.payload;
        })
    }
});

export default configuration.reducer;

export const { closeSnackBar, drawerAction } = configuration.actions;