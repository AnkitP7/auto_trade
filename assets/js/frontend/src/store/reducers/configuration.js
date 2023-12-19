// types
import { createSlice } from '@reduxjs/toolkit';
import { getConfiguration, createConfiguration, updateConfiguration } from 'services/configuration';

// initial state
const initialState = {
    list: {
        isLoading: false,
        data: [],
        error: {},
    },
    create: {
        isLoading: false,
        isVisible: false,
        actionType: 'Create',
        data: {},
        error: {},
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
                isVisible: action.payload.visible,
                actionType: action.payload.actionType,
            }
        },
        editAction: (state, action) => {
            state.create = {
                ...state.create,
                data: action.payload,
                actionType: 'Edit',
                isVisible: true,
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
                state.list.error = action.payload.error;
        })
        builder.addCase(getConfiguration.rejected, (state, action) => {
            state.list.error = action.payload;
        })
        builder.addCase(createConfiguration.pending, (state, action) => {
            state.create.isLoading = true;
        })
        builder.addCase(createConfiguration.fulfilled, (state, action) => {
            state.create.isLoading = false;
            if (action.payload.status) {
                state.create.data = action.payload.data;
                state.create.isVisible = false;
            }
            else {
                state.create.error = action.payload.error;
            }
        })
        builder.addCase(createConfiguration.rejected, (state, action) => {
            state.create.error = action.payload.error;
        })
        builder.addCase(updateConfiguration.pending, (state, action) => {
            state.create.isLoading = true;
        })
        builder.addCase(updateConfiguration.fulfilled, (state, action) => {
            state.create.isLoading = false;
            if (action.payload.status) {
                state.create.data = {}
                state.create.isVisible = false;
            }
            else {
                state.create.error = action.payload.error;
            }
        })
        builder.addCase(updateConfiguration.rejected, (state, action) => {
            state.create.error = action.payload.error;
        })
    }
});

export default configuration.reducer;

export const { closeSnackBar, drawerAction, editAction } = configuration.actions;