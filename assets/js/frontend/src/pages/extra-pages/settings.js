import { Grid, Stack, InputLabel, OutlinedInput, FormHelperText, Card, CardContent, Button, Avatar, Switch, List, ListItemButton, ListItemAvatar, ListItemText, Divider } from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';
import PasswordIcon from '@mui/icons-material/Password';
import SettingsInputComponentIcon from '@mui/icons-material/SettingsInputComponent';
import SaveIcon from '@mui/icons-material/Save';
import { Formik } from 'formik';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { getSettings, updateSettings } from 'services/settings.js'
import { changeSettingsItem } from 'store/reducers/settings';

const Settings = () => {

    return <SettingsForm />
}

const SettingOption = () => {

    const dispatch = useDispatch();
    const state = useSelector((state) => state.settings.user);

    return (
        <List sx={{ width: '100%', bgcolor: 'background.paper', marginTop: 15 }}>
            <ListItemButton key={0} selected={state.settingsItem === 0} onClick={(e) => dispatch(changeSettingsItem(0))}>
                <ListItemAvatar>
                    <Avatar>
                        <PersonIcon />
                    </Avatar>
                </ListItemAvatar>
                <ListItemText primary="User Profile" secondary="Profile Settings" />
            </ListItemButton>
            <ListItemButton key={1} selected={state.settingsItem === 1} onClick={(e) => dispatch(changeSettingsItem(1))}>
                <ListItemAvatar>
                    <Avatar>
                        <PasswordIcon />
                    </Avatar>
                </ListItemAvatar>
                <ListItemText primary="Password" secondary="Profile Security" />
            </ListItemButton>
            <ListItemButton key={2} selected={state.settingsItem === 2} onClick={(e) => dispatch(changeSettingsItem(2))}>
                <ListItemAvatar>
                    <Avatar>
                        <SettingsInputComponentIcon />
                    </Avatar>
                </ListItemAvatar>
                <ListItemText primary="Trade Configuration" secondary="Trade Secrets" />
            </ListItemButton>
        </List>

    )
}

const SettingsForm = (props) => {

    const dispatch = useDispatch();
    const state = useSelector((state) => state.settings.user);

    useEffect(() => {
        dispatch(getSettings());
        console.log(state);
    }, [])

    const formItems = [
        ((errors, values, handleBlur, handleChange, handleSubmit, isSubmitting, touched, setFieldValue) => (
            <>
                <Stack spacing={1} direction={"row"} justifyContent={"center"}>
                    <Avatar
                        alt={values.username}
                        src={null}
                        sx={{ width: 100, height: 100 }}
                    />
                </Stack>
                <Stack spacing={1}>
                    <InputLabel htmlFor="username">Username</InputLabel>
                    <OutlinedInput
                        label="Username"
                        value={values.username}
                        name="username"
                        onBlur={handleBlur}
                        onChange={handleChange}
                        disabled={true}
                        fullWidth
                        error={Boolean(touched.username && errors.username)}
                    />
                    {touched.username && errors.username && (
                        <FormHelperText error id="standard-weight-helper-text-">
                            {errors.username}
                        </FormHelperText>
                    )}
                </Stack>
                <Stack spacing={1}>
                    <InputLabel htmlFor="email">Email ID</InputLabel>
                    <OutlinedInput
                        value={values.email}
                        name="email"
                        disabled={true}
                        onBlur={handleBlur}
                        onChange={handleChange}
                        fullWidth
                        error={Boolean(touched.email && errors.email)}
                    />
                    {touched.email && errors.email && (
                        <FormHelperText error id="standard-weight-helper-text-">
                            {errors.email}
                        </FormHelperText>
                    )}
                </Stack>
                <Stack spacing={1}>
                    <InputLabel htmlFor="isActive">Active Account</InputLabel>
                    <Switch
                        checked={values.is_active}
                        onChange={(e) => setFieldValue('is_active', e.target.checked)}
                        inputProps={{ 'aria-label': 'controlled' }}
                    />
                    {touched.is_active && errors.is_active && (
                        <FormHelperText error id="standard-weight-helper-text-">
                            {errors.is_active}
                        </FormHelperText>
                    )}
                </Stack>
            </>
        )),
        ((errors, values, handleBlur, handleChange, handleSubmit, isSubmitting, touched, setFieldValue) => (
            <>
                <Stack spacing={1}>
                    <InputLabel htmlFor="currentPassword">Current Password</InputLabel>
                    <OutlinedInput
                        id="current_password"
                        value={""}
                        onBlur={handleBlur}
                        onChange={handleChange}
                        placeholder="Enter your current password"
                        fullWidth
                        error={Boolean(touched.current_password && errors.current_password)}
                    />
                    {touched.current_password && errors.current_password && (
                        <FormHelperText error id="standard-weight-helper-text-">
                            {errors.current_password}
                        </FormHelperText>
                    )}
                </Stack>
                <Stack spacing={1}>
                    <InputLabel htmlFor="New Password">New Password</InputLabel>
                    <OutlinedInput
                        id="new_password"
                        value={""}
                        name="new_password"
                        onBlur={handleBlur}
                        onChange={handleChange}
                        placeholder="Enter new password"
                        fullWidth
                        error={Boolean(touched.new_password && errors.new_password)}
                    />
                    {touched.new_password && errors.new_password && (
                        <FormHelperText error id="standard-weight-helper-text-">
                            {errors.new_password}
                        </FormHelperText>
                    )}
                </Stack>
            </>
        )),
        ((errors, values, handleBlur, handleChange, handleSubmit, isSubmitting, touched, setFieldValue) => (
            <>
                <Stack spacing={1}>
                    <InputLabel htmlFor="trade_user_id">User ID</InputLabel>
                    <OutlinedInput
                        id="trade_user_id"
                        value={values.trade_user_id}
                        name="trade_user_id"
                        onBlur={handleBlur}
                        onChange={handleChange}
                        placeholder="Enter your vendor User ID"
                        fullWidth
                        error={Boolean(touched.trade_user_id && errors.trade_user_id)}
                    />
                    {touched.trade_user_id && errors.trade_user_id && (
                        <FormHelperText error id="standard-weight-helper-text-">
                            {errors.trade_user_id}
                        </FormHelperText>
                    )}
                </Stack>
                <Stack spacing={1}>
                    <InputLabel htmlFor="API Key">API Key</InputLabel>
                    <OutlinedInput
                        id="trade_api_key"
                        value={values.trade_api_key}
                        name="trade_api_key"
                        onBlur={handleBlur}
                        onChange={handleChange}
                        placeholder="Enter valid API Key"
                        fullWidth
                        error={Boolean(touched.trade_api_key && errors.trade_api_key)}
                    />
                    {touched.trade_api_key && errors.trade_api_key && (
                        <FormHelperText error id="standard-weight-helper-text-">
                            {errors.trade_api_key}
                        </FormHelperText>
                    )}
                </Stack>
            </>
        )),
    ]

    return (
        <Grid container spacing={1}>
            <Grid item lg={24}>
                <Card>
                    <CardContent>
                        <Formik
                            enableReinitialize={true}
                            initialValues={{
                                ...state.data.user,
                                ...state.data,
                            }}
                            onSubmit={async (values, { setErrors, setStatus, setSubmitting }) => {
                                try {
                                    delete values['username'];
                                    delete values['email'];
                                    dispatch(updateSettings(values));
                                    setStatus({ success: false });
                                    setSubmitting(false);
                                } catch (err) {
                                    setStatus({ success: false });
                                    setErrors({ submit: err.message });
                                    setSubmitting(false);
                                }
                            }}
                        >
                            {({ errors, handleBlur, handleChange, handleSubmit, isSubmitting, touched, values, setFieldValue }) => (
                                <form onSubmit={handleSubmit}>
                                    <Grid container spacing={2} style={{ overflowY: 'auto' }}>
                                        <Grid item xs={12} lg={4}>
                                            <SettingOption />
                                        </Grid>
                                        <Divider orientation="vertical" flexItem />
                                        <Grid item xs={12} lg={6} rowSpacing={2}>
                                            {formItems[state.settingsItem](errors, values, handleBlur, handleChange, handleSubmit, isSubmitting, touched, setFieldValue)}
                                        </Grid>
                                    </Grid>
                                    <Divider />
                                    <Grid container justifyContent={"end"} marginInlineStart={-2} marginTop={2}>
                                        <Button startIcon={<SaveIcon />} variant='contained' type="submit">Save</Button>
                                    </Grid>
                                </form>
                            )}
                        </Formik>
                    </CardContent>
                </Card>
            </Grid>
        </Grid>
    )
}

export default Settings