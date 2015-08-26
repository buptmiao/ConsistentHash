ALL = consistent

all: $(ALL)

consistent : consistenthash.h hashring_example.cpp
	$(CXX)  $(CFLAGS) hashring_example.cpp -o consistent

.PHONY : clean

clean :
	rm $(ALL)
