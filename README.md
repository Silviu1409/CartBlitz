## CartBlitz este o aplicatie de tip eCommerce, destinata vanzarii de piese pentru PC.

### 10 business requirements

1. Aplicatia permite adaugarea, modificarea si afisarea clientilor.
2. Aplicatia permite adaugarea, modificarea si afisarea produselor.
3. Aplicatia permite adaugarea, modificarea si afisarea cosului de cumparaturi pentru un utilizator dat.
4. Aplicatia permite adaugarea, modificarea si afisarea comenzilor date de catre un utilizator dat.
5. Aplicatia permite adaugarea, modificarea si afisarea produselor din cosul de cumparaturi.
6. Aplicatia permite adaugarea, modificarea si afisarea produselor din comenzile date.
7. Aplicatia permite adaugarea, modificarea si afisarea recenziilor.
8. Aplicatia permite adaugarea, modificarea si afisarea garantiilor.
9. Aplicatia permite afisarea rezultatelor dorite din tabele sub diferite forme.
10. Aplicatia va utiliza o baza de date pentru a stoca datele.

### 5 main features

#### 1. Gestionarea clientilor

Administratorul poate adauga clienti noi in baza de date folosind un username, o adresa de email, o parola si numele complet, iar in cazul introducerii de date eronate, acestea pot fi modificate ulterior.

De asemenea, in ceea ce priveste afisarea, acestia vor fi afisati in functie de id, username sau vor fi sortati alfabetic crescator/descrescator in functie de numele complet.

#### 2. Gestionarea produselor

Administratorul poate adauga produse noi in baza de date folosind numele brand-ului, categoria din care acel produs face parte, o descriere a acelui produs, numele, pretul si cate produse sunt disponibile in stock, iar in cazul introducerii de date eronate, acestea pot fi modificate ulterior.

De asemenea, in ceea ce priveste afisarea, acestea vor fi afisate in functie de id, categorie, brand sau o marja de pret data.

In plus, administratorul poate schimba numarul de produse din stock intr-un mod facil, furnizand doar id-ul produsului si numarul de produse disponibile pe stoc.

#### 3. Gestionarea comenzilor/cosurilor de cumparaturi

Administratorul poate adauga cosuri de cumparaturi sau comenzi noi in baza de date folosind id-ul clientului corespunzator.

Un client nu poate sa aiba active in acelasi timp 2 sau mai multe cosuri de cumparaturi, iar in baza de date va fi retinuta si ora la care s-a facut ultima modificare asupra respectivei comenzi sau a respectivului cos de cumparaturi (in cazul completarii comenzii sau a adaugarii/modificarii/stergerii de produse).

De asemenea, in ceea ce priveste afisarea, acestea vor fi afisate in functie de id, id-ul clientului sau status (cos de cumparaturi sau comanda).

In plus administratorul poate completa o comanda (aceasta sa treaca cu statusul de la cos de cumparaturi la comanda) si sa modifice suma totala cu o valoare data.

#### 4. Gestionarea recenziilor

Administratorul poate adauga recenzii noi in baza de date folosind id-ul clientului care face recenzia, id-ul produsului pentru care se face recenzia, rating-ul acordat si un comentariu, iar in cazul introducerii de date eronate, acestea pot fi modificate ulterior.

De asemenea, in ceea ce priveste afisarea, acestea vor fi afisate in functie de id, id-ul clientului, id-ul produsului sau rating.

#### 5. Gestionarea garantiilor

Administratorul poate adauga garantii noi in baza de date folosind id-ul comenzii pentru care se face garantia, id-ul produsului pentru care se face garantia, si durata (in luni) a garantiei, iar in cazul introducerii de date eronate, acestea pot fi modificate ulterior.

De asemenea, in ceea ce priveste afisarea, acestea vor fi afisate in functie de id, id-ul clientului sau id-ul produsului.
