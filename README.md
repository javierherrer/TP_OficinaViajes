# oficina-viajes
El programa a desarrollar en Java consiste en una aplicación de asignación de asientos de autobuses, en una oficina de venta de billetes para el transporte regular de viajeros.

Tecnología de programación (30218) - Grado en Ingeniería Informática

E.U. Politécnica de Teruel - Universidad de Zaragoza

# Ejecución
1. En el directorio `ServidorOficinasViajes1.0/dist`:
```
java -jar "ServidorOficinasViajes1.0.jar" 
```

2. En el directorio `OficinaViajes3.3/dist`:
```
java -jar "OficinaViajes3.3.jar" 
```

# Especificaciones de implementación
- Arquitectura MVC (Modelo-vista-controlador).
- Diseño mantenible, extensible, reutilizable y eficiente.
- Patrones arquitecturales: _Singleton_, factorías, _Observer_...
- Biblioteca _Swing_ de Java para la interfaz gráfica de ventanas.
- Arquitectura cliente-servidor mediante _sockets_ con _pool_ de _threads_.
- Sincronización de las vistas mediante conexión _push_ con _long polling_.

# Ficheros
- `ServidorOficinasViajes1.0/viajes.txt`: información sobre la programación de los viajes.
- `ServidorOficinasViajes1.0/autobuses.txt`: distribución y numeración de asientos, igual para todos los autobuses.
- `ServidorOficinasViajes1.0/config.properties`: número de hilos y puerto del servidor.
- `OficinaViajes3.3/hojaViaje.txt`: hoja de viaje generada para el conductor con los asientos asignados, así como con los datos de los viajeros que los ocuparán.
- `OficinaViajes3.3/config.properties`: host y puerto del servidor.
