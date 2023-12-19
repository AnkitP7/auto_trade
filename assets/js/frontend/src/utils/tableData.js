const DATA_CONFIG = {
    configuration: {
        columns: [
            {
                id: 'tag',
                label: 'Tag',
            },
            {
                id: 'indexName',
                label: 'Index',
            },
            {
                id: 'daysOfWeek',
                label: 'Days of Week',
            },
            {
                id: 'startTime',
                label: 'Start Time',
            },
            {
                id: 'endTime',
                label: 'End Time',
            },
            {
                id: 'quantity',
                label: 'Quantity',
            },
            {
                id: 'entryCriteria',
                label: 'Entry criteria',
            },
            {
                id: 'entryCriteriaValue',
                label: 'Entry Criteria Value',
            },
            {
                id: 'stopLossType',
                label: 'Stop Loss Type',
                render: (row) => row.stopLossType ? "Percentage" : "Flat"
            },
            {
                id: 'stopLossValue',
                label: 'Stop Loss Value',
            },
            // {
            //     id: 'created_datetime',
            //     label: 'Created At'
            // }
        ]
    }
}

export { DATA_CONFIG }