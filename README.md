# AplicacionFacturas
Aplicación gestión de facturas practica DAM Acceso a datos

## Caracteristicas

*Acceso a base de datos en servidor Oracle

*Login de usuario y contraseña

*Creación, modificación, añadido de todas las funciones de la aplicación (Clientes, Articulos, Facturas, Lineas de factura)

*Visualizacin de datos en JTable modificado
    
**Creacion de JTable propio heredando de la clase JTable dandole funciones extra para tener la posibilidad de mostrar todos los  datos unicamente pasandole un objeto de la clase               (ResultSet), el JTable recoge este resultSet y accede a sus metadatos (ResultSetMetaData) para sacar: nombres de columnas, cantidad de elementos, etc
    
*(En servidor Oracle) Disparadores para mantener controlado el stockage de los articulos cada vez que se haga una insercion o un borrado de una o varias lineas de facturas

