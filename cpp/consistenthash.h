
#ifndef _CONSISTENT_H
#define _CONSISTENT_H

#include <map>
#include <string>
#include <sstream>

namespace
{

class StringifyException
{
};

template <class T>
std::string Stringify(const T& t)
{
	std::ostringstream os;
	if (!(os << t))
	{
		throw StringifyException();
	}
	return os.str();
}

template <>
std::string Stringify(const std::string& str)
{
	return str;
}

template <>
std::string Stringify(const char* const& str)
{
	return str;
}
struct SdbmHash
{
	size_t operator()(const char * str) const
	{
		size_t hash = 0;
		int c;

		while ((c = *str++)) {
			hash = c + (hash << 6) + (hash << 16) - hash;
		}

		return hash;
	}
};
}

namespace ConsistentHash
{

class EmptyRingException
{
};

template <class Node, class Data, class Hash = SdbmHash >

class HashRing
{
public:
	typedef std::map<size_t, Node> NodeMap;

	HashRing(unsigned int replicas)
		: replicas_(replicas), hash_(SdbmHash())
	{
	}

	HashRing(unsigned int replicas, const Hash& hash)
		: replicas_(replicas), hash_(hash)
	{
	}

	size_t add(const Node& node);
	void remove(const Node& node);
	const Node& get(const Data& data) const;

private:
	NodeMap ring_;
	const unsigned int replicas_;
	Hash hash_;
};

template <class Node, class Data, class Hash>
size_t HashRing<Node, Data, Hash>::add(const Node& node)
{
	size_t hash;
	std::string nodestr = Stringify(node);
	for (unsigned int r = 0; r < replicas_; r++) {
		hash = hash_((nodestr + Stringify(r)).c_str());
		ring_[hash] = node;
	}
	return hash;
}

template <class Node, class Data, class Hash>
void HashRing<Node, Data, Hash>::remove(const Node& node)
{
	std::string nodestr = Stringify(node);
	for (unsigned int r = 0; r < replicas_; r++) {
		size_t hash = hash_((nodestr + Stringify(r)).c_str());
		ring_.erase(hash);
	}
}

template <class Node, class Data, class Hash>
const Node& HashRing<Node, Data, Hash>::get(const Data& data) const
{
	if (ring_.empty()) {
		throw EmptyRingException();
	}
	size_t hash = hash_(Stringify(data).c_str());
	typename NodeMap::const_iterator it;
	
	it = ring_.lower_bound(hash);
	if (it == ring_.end()) {
		it = ring_.begin();
	}
	return it->second;
}

}

#endif // CONSISTENT_H
