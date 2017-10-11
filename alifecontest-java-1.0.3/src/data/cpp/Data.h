#ifndef DATA_H
#define DATA_H

 /*
  * The Position of the microorganism
  */
struct Position{
	int x;
	int y;
};

 /*
  * This struct represents the movement of a microorganism.
  * The movements are relatives:
  */
struct Movement{
	int dx;
	int dy;
};

#endif
