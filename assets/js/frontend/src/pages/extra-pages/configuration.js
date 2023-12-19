import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
// material-ui
import { Grid, Stack, Typography, InputLabel, OutlinedInput, FormControlLabel, Switch, Button, Select, MenuItem, SwipeableDrawer, Card, CircularProgress, LinearProgress, FormHelperText, Chip, IconButton } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import HighlightOffIcon from '@mui/icons-material/HighlightOff';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { TimePicker } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import AnimateButton from 'components/@extended/AnimateButton';
import { Formik } from 'formik';
// project import
import DataTable from 'components/dataTable';
import { drawerAction, editAction } from 'store/reducers/configuration';
import { PlusOutlined } from '@ant-design/icons';
import { getConfiguration, createConfiguration, updateConfiguration } from 'services/configuration';
import { DATA_CONFIG } from 'utils/tableData';

import dayjs from 'dayjs';
var customParseFormat = require('dayjs/plugin/customParseFormat');
dayjs.extend(customParseFormat);

const DEFAULT_ACTION = 'Create';

const Configuration = () => {

  const dispatch = useDispatch();
  const state = useSelector((state) => state.configuration);
  const actions = [
    {
      id: 'is_enabled',
      label: 'Active',
      render: (row) => <Chip style={{ borderRadius: 50 }} icon={row.isEnabled ? <CheckCircleIcon /> : <HighlightOffIcon />} label={row.isEnabled ? "Yes" : "No"} />
    },
    {
      id: 'actions',
      label: 'Actions',
      render: (row) => {
        return (
          <IconButton color='secondary' aria-label="edit" onClick={(e) => {
            e.preventDefault();
            dispatch(editAction(row));
          }}>
            <EditIcon />
          </IconButton>
        )
      }
    }
  ];

  const columns = [...DATA_CONFIG.configuration.columns, ...actions]

  let dataTableProps = {
    columns,
    rows: state.list.data,
  }

  useEffect(() => {
    dispatch(getConfiguration());
  }, [])

  return (
    <Grid container rowSpacing={2} columnSpacing={2} justifyContent={'flex-end'}>
      <Grid item>
        <Button variant="contained" startIcon={<PlusOutlined />} onClick={() => { dispatch(drawerAction({ visible: true, actionType: DEFAULT_ACTION })) }}>
          Create
        </Button>
      </Grid>
      <Grid item lg={24} md={12} xs={6}>
        {state.list.isLoading ? <LinearProgress /> : null}
        <DataTable {...dataTableProps} />
      </Grid>
      <Grid item lg={24} md={12} xs={6}>
        <ConfigurationForm />
      </Grid>
    </Grid>
  )
}

const ConfigurationForm = () => {

  const dispatch = useDispatch();
  const state = useSelector((state) => state.configuration.create);
  const ACTION_MAP = {
    'Edit': 1,
    'Create': 0,
  }

  return (
    <SwipeableDrawer
      sx={{
        width: '20%',
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          marginTop: '2.5%',
          width: '20%',
          boxSizing: 'border-box',
        },
      }}
      anchor={"right"}
      open={state.isVisible}
      hideBackdrop={true}
      onClose={() => dispatch(drawerAction({ visible: false, actionType: DEFAULT_ACTION }))}
      onOpen={() => dispatch(drawerAction({ visible: true, actionType: DEFAULT_ACTION }))}
    >
      <Card style={{ margin: 30, border: 'none', boxShadow: "none" }}>
        <Formik
          enableReinitialize
          initialValues={{
            tag: '',
            indexName: '',
            daysOfWeek: 1,
            formattedStartTime: ACTION_MAP[state.actionType] ? state.data.startTime : '',
            formattedEndTime: ACTION_MAP[state.actionType] ? state.data.endTime : '',
            quantity: null,
            entryCriteria: null,
            entryCriteriaValue: null,
            stopLossType: null,
            stopLossValue: null,
            isEnabled: false,
            ...state.data,
            startTime: ACTION_MAP[state.actionType] ? dayjs(state.data.startTime, "HH:mm:ss") : '',
            endTime: ACTION_MAP[state.actionType] ? dayjs(state.data.endTime, "HH:mm:ss") : '',
          }}
          onSubmit={async (values, { setErrors, setStatus, setSubmitting }) => {
            try {
              if (ACTION_MAP[state.actionType])
                dispatch(updateConfiguration(values))
              else
                dispatch(createConfiguration(values))
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
            <form noValidate onSubmit={handleSubmit}>
              <Grid container lg={24} spacing={1} style={{ overflowY: 'auto' }}>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Tag">Tag</InputLabel>
                    <OutlinedInput
                      id="tag"
                      value={values.tag}
                      name="tag"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter a tag to identify your configuration"
                      fullWidth
                      error={Boolean(touched.tag && errors.tag)}
                    />
                    {touched.tag && errors.tag && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.tag}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Index Name">Index Name</InputLabel>
                    <OutlinedInput
                      id="indexName"
                      value={values.indexName}
                      name="indexName"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter valid index name"
                      fullWidth
                      error={Boolean(touched.indexName && errors.indexName)}
                    />
                    {touched.indexName && errors.indexName && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.indexName}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="daysOfWeek">Days of Week</InputLabel>
                    <Select
                      name="daysOfWeek"
                      value={values.daysOfWeek}
                      label="Age"
                      onChange={handleChange}
                    >
                      {[1, 2, 3, 4, 5, 6,].map((day, index) => <MenuItem key={day} value={day}>{day}</MenuItem>)}
                    </Select>
                    {touched.daysOfWeek && errors.daysOfWeek && (
                      <FormHelperText error id="standard-weight-helper-text-daysOfWeek">
                        {errors.daysOfWeek}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Start Time">Start Time</InputLabel>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                      <TimePicker
                        label="Start Time"
                        id="startTime"
                        ampm={false}
                        value={values.startTime}
                        onChange={(value) => { console.log(dayjs(value).format('HH:MM:ss')); setFieldValue("startTime", value, true); setFieldValue("formattedStartTime", dayjs(value).format('HH:mm:ss')) }}
                      />
                    </LocalizationProvider>
                    {touched.startTime && errors.startTime && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.startTime}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="End Time">End Time</InputLabel>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                      <TimePicker
                        label="End Time"
                        id="endTime"
                        ampm={false}
                        value={values.endTime}
                        onChange={(value) => { setFieldValue("endTime", value, true); setFieldValue("formattedEndTime", dayjs(value).format('HH:mm:ss')) }}
                      />
                    </LocalizationProvider>
                    {touched.endTime && errors.endTime && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.endTime}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Quantity">Quantity</InputLabel>
                    <OutlinedInput
                      id="quantity"
                      value={values.quantity}
                      name="quantity"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter valid quantity"
                      fullWidth
                      error={Boolean(touched.quantity && errors.quantity)}
                    />
                    {touched.quantity && errors.quantity && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.quantity}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Entry Criteria">Entry Criteria</InputLabel>
                    <OutlinedInput
                      id="entryCriteria"
                      value={values.entryCriteria}
                      name="entryCriteria"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter entry criteria"
                      fullWidth
                      error={Boolean(touched.entryCriteria && errors.entryCriteria)}
                    />
                    {touched.entryCriteria && errors.entryCriteria && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.entryCriteria}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Entry Criteria value">Entry Criteria Value</InputLabel>
                    <OutlinedInput
                      id="entryCriteriaValue"
                      value={values.entryCriteriaValue}
                      name="entryCriteriaValue"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter enter entry criteria value"
                      fullWidth
                      error={Boolean(touched.entryCriteriaValue && errors.entryCriteriaValue)}
                    />
                    {touched.entryCriteriaValue && errors.entryCriteriaValue && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.entryCriteriaValue}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Stop Loss Type">Stop Loss type</InputLabel>
                    <Select
                      name="stopLossType"
                      value={Number(values.stopLossType)}
                      label="Age"
                      onChange={handleChange}
                    >
                      <MenuItem value={0}>Fixed</MenuItem>
                      <MenuItem value={1}>Percentage</MenuItem>
                    </Select>
                    {touched.stopLossType && errors.stopLossType && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.stopLossType}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} lg={24}>
                  <Stack spacing={1}>
                    <InputLabel htmlFor="Stop Loss value">Stop Loss Value</InputLabel>
                    <OutlinedInput
                      id="stopLossValue"
                      value={values.stopLossValue}
                      name="stopLossValue"
                      onBlur={handleBlur}
                      onChange={handleChange}
                      placeholder="Enter enter stop los value"
                      fullWidth
                      error={Boolean(touched.stopLossValue && errors.stopLossValue)}
                    />
                    {touched.stopLossValue && errors.stopLossValue && (
                      <FormHelperText error id="standard-weight-helper-text-">
                        {errors.stopLossValue}
                      </FormHelperText>
                    )}
                  </Stack>
                </Grid>
                <Grid item xs={12} sx={{ mt: -1 }} lg={24}>
                  <Stack direction="row">
                    <FormControlLabel
                      name="isEnabled"
                      value={values.isEnabled}
                      control={
                        <Switch checked={values.isEnabled} size="large" />
                      }
                      onChange={(e) => setFieldValue('isEnabled', e.target.checked )}
                      label={<Typography variant="h6">Enabled</Typography>}
                    />
                  </Stack>
                </Grid>
                {errors.submit && (
                  <Grid item xs={12}>
                    <FormHelperText error>{errors.submit}</FormHelperText>
                  </Grid>
                )}
                <Grid item xs={12} lg={12}>
                  <AnimateButton>
                    <Button variant="contained" disabled={state.isLoading} fullWidth size="large" type="submit" color="primary">
                      {state.isLoading ? <CircularProgress size={15} /> : null}
                      {state.actionType}
                    </Button>
                  </AnimateButton>
                </Grid>
              </Grid>
            </form>
          )}
        </Formik>
      </Card>
    </SwipeableDrawer>
  )
}

export default Configuration;
