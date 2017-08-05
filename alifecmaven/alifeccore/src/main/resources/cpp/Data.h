#ifndef DATA_H
#define DATA_H

 // Posicion de un Microorganismo.
struct Position{
	int x;
	int y;
};
// Representa un movimiento  relativo a una posicion.
// El movimient puede ser -1, 0, 1. 
struct Movement{
	int dx;
	int dy;
};

#endif
