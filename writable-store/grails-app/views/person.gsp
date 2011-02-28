<html>

<head>
  <title>Person editor</title>
  <meta name="layout" content="ext"/>

  <script type="text/javascript">
Ext.onReady(function() {

var store = new Ext.data.JsonStore({
    url: '/example/api/person',
    restful: true,
    remoteStore: true,
    remoteSort: true,
    root: 'data',
    totalProperty: 'count',
    messageProperty: 'message',
    fields: [
        {name: 'id'},
        {name: 'firstName', allowBlank: false},
        {name: 'lastName', allowBlank: false}
    ],
    paramNames: { start: 'offset', limit: 'max', sort: 'sort', dir: 'order' },
    writer: new Ext.data.JsonWriter({
      encode: false
    })
});

// load the store immeditately
store.load();

// Let's pretend we rendered our grid-columns with meta-data from our ORM framework.
var userColumns =  [
    {header: "ID", width: 40, sortable: true, dataIndex: 'id'},
    {header: "First name", width: 50, sortable: true, dataIndex: 'firstName', editor: new Ext.form.TextField({})},
    {header: "Last name", width: 50, sortable: true, dataIndex: 'lastName', editor: new Ext.form.TextField({})}
];

Ext.QuickTips.init();

// Create a typical GridPanel with RowEditor plugin
var userGrid = new Ext.grid.EditorGridPanel({
    renderTo: Ext.getBody(),
    iconCls: 'icon-grid',
    frame: true,
    title: 'Users',
    height: 300,
    store: store,
    columns : userColumns,
    tbar: [{
        text: 'Add',
        iconCls: 'silk-add',
        handler: onAdd
    }, '-', {
        text: 'Delete',
        iconCls: 'silk-delete',
        handler: onDelete
    }, '-'],
    viewConfig: {
        forceFit: true
    },
    bbar: new Ext.PagingToolbar({
        pageSize: 1,
        store: store,
        displayInfo: true,
        displayMsg: 'Displaying topics {0} - {1} of {2}',
        emptyMsg: "No topics to display",
        items:[
            '-', {
            pressed: true,
            enableToggle:true,
            text: 'Show Preview',
            cls: 'x-btn-text-icon details',
            toggleHandler: function(btn, pressed){
                var view = grid.getView();
                view.showPreview = pressed;
                view.refresh();
            }
        }]
    })
});

function onAdd(btn, ev) {
    var u = new userGrid.store.recordType({
        firstName : '',
        lastName: ''
    });
    userGrid.stopEditing();
    userGrid.store.insert(0, u);
    userGrid.startEditing(0, 0);
}

function onDelete() {
    var index = userGrid.getSelectionModel().getSelectedCell();
    if (!index) return false;
    var rec = userGrid.store.getAt(index[0]);
    if (!rec) return false;
    userGrid.store.remove(rec);
}

});
  </script>
</head>

<body>
</body>

</html>
