/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacionfacturas;

import java.awt.Color;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author manrique
 */
public class FacturasWindow extends javax.swing.JFrame {

    protected Connection connection;
    protected ResultSet resultSet, resultSetClientes, resultSetArticulos, resultSetLinFac;
    protected Statement statement;
    protected JFrame loginWindow;
    
    //to get the client
    protected String clienteName, pagada;    
    protected int numFactura, totalFactura=0, codigoCliente;            //0 NO pagada 1 pagada
    
    /**
     * Creates new form FacturasWindow
     */
    public FacturasWindow(Connection connectionE, JFrame ventanaLoginE) {
        initComponents();
        connection = connectionE;       //get the connection
        loginWindow = ventanaLoginE;    //get the login window
        loginWindow.setVisible(false);  //show
        try {
            getTable();
            getClientesAvailables();
        } catch (SQLException ex) {
            Logger.getLogger(FacturasWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end constructor

    public void getClientesAvailables() throws SQLException {
        resultSetClientes = statement.executeQuery("SELECT CODIGO, NOMBRE FROM CLIENTES");
        while (resultSetClientes.next()) {
            clientesCombo.addItem(resultSetClientes.getString("NOMBRE"));
        }//add clientes in clientes
    }//get possible clients for combobox     
    
    public void getArticulosAvailables() throws SQLException {
        //only get the articulos with stocks available
        resultSetArticulos = statement.executeQuery("SELECT DESCRIPCION FROM ARTICULOS WHERE STOCK>0");
        while(resultSetArticulos.next()) {
            articulosCombo.addItem(resultSetArticulos.getString("DESCRIPCION"));
        }
    }//get possible articulos for combobox
    
    public String getReferenciaArticulo(String descripcionDeArticulo) throws SQLException {
        resultSet = statement.executeQuery("SELECT REFERENCIA FROM ARTICULOS WHERE DESCRIPCION='"+descripcionDeArticulo+"'");
        while (resultSet.next()) {
            return resultSet.getString("REFERENCIA");
        }
        return "";
    }
    
    public boolean getDataFromTexts() {
        if (numFacturaText.getText().length()>0) {
            numFactura = Integer.parseInt(numFacturaText.getText());
            clienteName = clientesCombo.getSelectedItem().toString();
            if (estadoCombo.getSelectedIndex()==0) {
                pagada="N";   //NO PAGADA
            } else {
                pagada="S";   //PAGADA
            }//get the state
            try {
                //get the codigo from cliente
                resultSet = statement.executeQuery("SELECT CODIGO FROM CLIENTES WHERE NOMBRE='"+clienteName+"'");
                while (resultSet.next()) {
                    codigoCliente = Integer.parseInt( resultSet.getString("CODIGO") );
                }//get the codigo
            } catch (SQLException ex) {
                Logger.getLogger(FacturasWindow.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        } else {return false;}        
    }//get all necesary data
    
    public void createNewFactura() throws SQLException {
        
        if (getDataFromTexts()) {
            statement.executeUpdate("INSERT INTO FACTURAS VALUES ("+numFactura+", SYSDATE, "+codigoCliente+", "+totalFactura+", '"+pagada+"')");            
            //prepare the dialog
            linFacturasDialog.pack();
            linFacturasDialog.setModal(true);
            facturaText.setText(numFactura+"");
            getArticulosAvailables();
            linFacturasDialog.setVisible(true);
        }// check all
    }//create a DEFAULT factura
    
    public void createNewLinFactura(){
        try {
            if (idText.getText().length()>0 && cantidadText.getText().length()>0 && descText.getText().length() >0 ) {
                int idLin = Integer.parseInt(idText.getText());
                int cantidad = Integer.parseInt(cantidadText.getText());
                int descuento = Integer.parseInt(descText.getText());
                try {
                    statement.executeUpdate(""
                            + "INSERT INTO LINFACTURAS VALUES("+idLin+","+numFactura+",'"+getReferenciaArticulo(articulosCombo.getSelectedItem().toString())+"', "+cantidad+", "+descuento+")");
                    JOptionPane.showMessageDialog(this, "Nueva Linea creada con éxito");
                } catch (SQLIntegrityConstraintViolationException ex) {
                    JOptionPane.showMessageDialog(this, "El ID de linea de factura ya existe");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al crear una linea de factura");
                    Logger.getLogger(FacturasWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                idText.setText("");
                cantidadText.setText("");
                descText.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Rellena los campos para crear una linea de factura");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID, cantidad y descuento deben ser valores numéricos");
        }
    }//create a new LINFACTURAS
    
    public boolean deleteFacturasAndLinfac(int numFactura) {
        try {            
            //search all linfactura from this factura           
            resultSetLinFac = statement.executeQuery("SELECT * FROM LINFACTURAS WHERE NFACTURA="+numFactura+"");
            while(resultSetLinFac.next()) {
                statement.executeUpdate("DELETE FROM LINFACTURAS WHERE NFACTURA="+numFactura+"");
            }
            //delete the factura
            statement.executeUpdate("DELETE FROM FACTURAS WHERE NFACTURA="+numFactura+"");            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(FacturasWindow.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }//delete factura and linfacturas from this factura
    
    public void prepareLinFacturas() throws SQLException {
        getArticulosAvailables();                
    }
    
    public void getTable() throws SQLException {
        statement = connection.createStatement();   //get the statement
        resultSet = statement.executeQuery("SELECT * FROM VIEW_FACTURAS_CLIENTES ORDER BY NFACTURA");
        tableFacturas.startTable();
        tableFacturas.createTheTable(resultSet, Color.black, Color.white);        
    }//create the table
    
    public boolean getTableLinFacturas(int numDeFactura) throws SQLException {
        
        statement = connection.createStatement();   //get the statement
        resultSet = statement.executeQuery("SELECT * FROM VIEW_LINFACTURA_ARTICULO WHERE NFACTURA="+numDeFactura+" ORDER BY ID");
        linFacturasTable.startTable();
        linFacturasTable.createTheTable(resultSet, Color.GRAY, Color.WHITE);
        totalPagarLabel.setText(getTotalAPagar()+" €");
        if (linFacturasTable.getRowCount()>0 ) {
            return true;
        } else {return false;}        
    }//create a table to see all the linfacturas from specific factura
    
    public int getTotalAPagar() {
        int total=0;
        for (int i=0;i<linFacturasTable.getRowCount();i++) {
            total=total+Integer.parseInt(linFacturasTable.getValueAt(i, 7).toString());
        }
        return total;
    }
    
    public void payFactura(int numDeFactura) {
        try {
            statement.executeUpdate("UPDATE FACTURAS SET PAGADA = 'S' WHERE NFACTURA="+numDeFactura+"");
            JOptionPane.showMessageDialog(this, "factura pagada correctamente");
            getTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al pagar la factura "+ex.getMessage());
            Logger.getLogger(FacturasWindow.class.getName()).log(Level.SEVERE, null, ex);            
        }
    }
    
    public void clearTextFields() {
        numFacturaText.setText("");
        estadoCombo.setSelectedIndex(0);
    }//clear textfields
    
    private FacturasWindow() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        linFacturasDialog = new javax.swing.JDialog();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        idText = new javax.swing.JTextField();
        articulosCombo = new javax.swing.JComboBox<>();
        cantidadText = new javax.swing.JTextField();
        descText = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        facturaText = new javax.swing.JLabel();
        lineasFacturaDialog = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        try {
            linFacturasTable = new jtUserView.JTableUserView();
        } catch (java.sql.SQLException e1) {
            e1.printStackTrace();
        }
        jLabel10 = new javax.swing.JLabel();
        numLinFac = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        totalPagarLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        try {
            tableFacturas = new jtUserView.JTableUserView();
        } catch (java.sql.SQLException e1) {
            e1.printStackTrace();
        }
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        numFacturaText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        clientesCombo = new javax.swing.JComboBox<>();
        estadoCombo = new javax.swing.JComboBox<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();

        linFacturasDialog.setTitle("Linea de factura");

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jLabel2.setText("Nueva linea de factura:");

        jLabel4.setText("Id");

        jLabel7.setText("Articulo");

        jLabel8.setText("Cantidad");

        jLabel9.setText("Descuento");

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Add-item-icon.png"))); // NOI18N
        jButton5.setText("Añadir linea");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/CleanMyMac-1-icon.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Go-back-icon.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        facturaText.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        facturaText.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout linFacturasDialogLayout = new javax.swing.GroupLayout(linFacturasDialog.getContentPane());
        linFacturasDialog.getContentPane().setLayout(linFacturasDialogLayout);
        linFacturasDialogLayout.setHorizontalGroup(
            linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linFacturasDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(linFacturasDialogLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(facturaText))
                    .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(linFacturasDialogLayout.createSequentialGroup()
                            .addComponent(jButton4)
                            .addGap(30, 30, 30)
                            .addComponent(jButton5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6))
                        .addGroup(linFacturasDialogLayout.createSequentialGroup()
                            .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel7)
                                .addComponent(jLabel8)
                                .addComponent(jLabel9))
                            .addGap(41, 41, 41)
                            .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(idText)
                                .addComponent(articulosCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cantidadText)
                                .addComponent(descText, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        linFacturasDialogLayout.setVerticalGroup(
            linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linFacturasDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(facturaText))
                .addGap(16, 16, 16)
                .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(idText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(articulosCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(cantidadText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(descText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(linFacturasDialogLayout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addContainerGap(24, Short.MAX_VALUE))
                    .addGroup(linFacturasDialogLayout.createSequentialGroup()
                        .addGroup(linFacturasDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addComponent(jButton5))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        linFacturasTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        linFacturasTable.setFont(new java.awt.Font("STIXGeneral", 0, 18)); // NOI18N
        jScrollPane2.setViewportView(linFacturasTable);

        jLabel10.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        jLabel10.setText("Factura");

        numLinFac.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        numLinFac.setText("jLabel11");

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/payment-icon.png"))); // NOI18N
        jButton7.setText("Pagar factura");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        totalPagarLabel.setFont(new java.awt.Font("Ubuntu Light", 1, 18)); // NOI18N

        javax.swing.GroupLayout lineasFacturaDialogLayout = new javax.swing.GroupLayout(lineasFacturaDialog.getContentPane());
        lineasFacturaDialog.getContentPane().setLayout(lineasFacturaDialogLayout);
        lineasFacturaDialogLayout.setHorizontalGroup(
            lineasFacturaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lineasFacturaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lineasFacturaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addGroup(lineasFacturaDialogLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numLinFac)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(lineasFacturaDialogLayout.createSequentialGroup()
                .addGap(254, 254, 254)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalPagarLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lineasFacturaDialogLayout.setVerticalGroup(
            lineasFacturaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lineasFacturaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lineasFacturaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(numLinFac))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(lineasFacturaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(totalPagarLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Go-back-icon.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Log-Out-icon.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        tableFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tableFacturas);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Numero factura");

        jLabel3.setText("Cliente");

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel5.setText("Factura");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/CleanMyMac-1-icon.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel6.setText("Estado");

        estadoCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No pagada", "Pagada" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(141, 141, 141))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(numFacturaText))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addGap(80, 80, 80)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clientesCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(estadoCombo, 0, 177, Short.MAX_VALUE)))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(numFacturaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clientesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(estadoCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Zoom-icon.png"))); // NOI18N
        jMenu1.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu1MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenuBar1.add(jMenu1);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add-Icon.png"))); // NOI18N
        jMenu2.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu2MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenuBar1.add(jMenu2);

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Button-Delete-icon.png"))); // NOI18N
        jMenu4.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu4MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu1MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu1MenuSelected
        int numberOfFactura = Integer.parseInt(JOptionPane.showInputDialog("Introduce numero de factura para ver sus lineas de factura"));
        try {
            if (getTableLinFacturas(numberOfFactura)) {
                numLinFac.setText(numberOfFactura+"");
                lineasFacturaDialog.pack();
                lineasFacturaDialog.setModal(true);
                lineasFacturaDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "No existen lineas de facturas para la factura número: "+numberOfFactura);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(FacturasWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenu1MenuSelected

    private void jMenu2MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu2MenuSelected
        try {
            createNewFactura();            
            getTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error "+ex.getMessage());
            Logger.getLogger(FacturasWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenu2MenuSelected

    private void jMenu4MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu4MenuSelected
        try {
            int numFacDelete = Integer.parseInt(JOptionPane.showInputDialog("Introduce numero de factura para borrar (Esto borrará todas sus lineas de facturaa)"));
            if (deleteFacturasAndLinfac(numFacDelete)) {
                JOptionPane.showMessageDialog(this, "La factura "+numFacDelete+" fue borrada con sus lineas de facturas correspondiente");
                getTable();                
            }
        }catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce un numero de factura correcto");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar la tabla");
            Logger.getLogger(FacturasWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenu4MenuSelected

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //go back to login
        loginWindow.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            //close connection and the programm
            connection.close();
            System.exit(0);
        } catch (SQLException ex) {
            Logger.getLogger(ArticulosWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        clearTextFields();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        idText.setText("");
        cantidadText.setText("");
        descText.setText("");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        linFacturasDialog.setVisible(false);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        
        createNewLinFactura();
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        payFactura( Integer.parseInt( numLinFac.getText() ) );
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FacturasWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FacturasWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FacturasWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FacturasWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FacturasWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> articulosCombo;
    private javax.swing.JTextField cantidadText;
    private javax.swing.JComboBox<String> clientesCombo;
    private javax.swing.JTextField descText;
    private javax.swing.JComboBox<String> estadoCombo;
    private javax.swing.JLabel facturaText;
    private javax.swing.JTextField idText;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JDialog linFacturasDialog;
    private jtUserView.JTableUserView linFacturasTable;
    private javax.swing.JDialog lineasFacturaDialog;
    private javax.swing.JTextField numFacturaText;
    private javax.swing.JLabel numLinFac;
    private jtUserView.JTableUserView tableFacturas;
    private javax.swing.JLabel totalPagarLabel;
    // End of variables declaration//GEN-END:variables
}
