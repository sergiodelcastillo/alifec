#include <iostream>
#include <vector>
using namespace std;

class Microorganism{
protected:
	string text;
	
public:

	virtual void setText(string text){
		this->text = text;
	};
	virtual string getText(){
		return text;
	}
};

class AdvancedMO: public Microorganism{
	
};

template < typename MO > class CppColony{
protected:
	vector<MO > mos;

public:
	void createMO(string text){
		MO mo = MO();
		mo.setText(text);
		mos.push_back(mo);
	};
	vector<MO > getMOs(){
		return mos;
	}
};

int main(){

	cout << "asdf"<< endl;
	CppColony<AdvancedMO> colony;
	vector<CppColony<Microorganism> *> colonies;

	colony.createMO("mo_a");
	colony.createMO("mo_b");

	vector<AdvancedMO> list = colony.getMOs();
	
	colonies.push_back(dynamic_cast<CppColony<AdvancedMO>*>(&colony));
	

	for(int i = 0; i< list.size(); i++){
		cout << list[i].getText() << endl;
	}
	return 0;
}

