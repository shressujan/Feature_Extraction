module CustomerOrderExtended
open Declaration

one sig Company extends Class{}{
attrSet = Name+RegNo
one parent
parent in Customer
id = Id
isAbstract = No
}

one sig Name extends string{}
one sig RegNo extends Integer{}

one sig Item extends Class{}{
attrSet = Id+price+quantity
id=Id
no parent
isAbstract = No
}

one sig Id extends Integer{}
one sig price extends Integer{}
one sig quantity extends Integer{}

one sig Order extends Class{}{
attrSet = Id+date+status
id=Id
no parent
isAbstract = No
}

one sig date extends Integer{}
one sig status extends string{}

one sig Person extends Class{}{
attrSet = firstName+LastName
one parent
parent in Customer
id = Id
isAbstract = No
}

one sig firstName extends string{}
one sig LastName extends string{}

one sig Customer extends Class{}{
attrSet = Id+address+phone
id=Id
no parent
isAbstract = No
}

one sig address extends string{}
one sig phone extends Integer{}

one sig CustomerOrderAssociation extends Association{}{
src = Customer
dst= Order
src_multiplicity = ONE
dst_multiplicity = MANY
}

one sig OrderItemAssociation extends Association{}{
src = Order
dst= Item
src_multiplicity = ONE
dst_multiplicity = MANY
}

pred show{}
run show for 37
