Ćwiczenie 11. Monitorowanie i zmiana przebiegu działania aplikacji z użyciem JMX.
Podstawową zmianą jest ustalenie maksymalnego rozmiary pamięci podręcznej. Jeśli chcemy dodać nowy wpis, a pamięć podręczna posiada maksymalną liczbę wpisów, to trzeba usunąć jakiś istniejący wpis. Nie ma też konieczności używania miękkich referencji (mogą być zwykłe), bo zarządzanie pamięcią będzie ręczne, a nie automatyczne z użyciem Garbage Collectora. Można oprzeć się na modyfikacji programu z ćwiczenia 3 (aczkolwiek czasami może być prościej napisać program od 0, bo części elementów jednak w ćwiczeniu 11 nie ma).

Korzystając z JMX należy stworzyć w aplikacji ziarenko MBean lub MXBean posiadające:

Właściwość pozwalającą ustawić liczbę wątków (od 0 wzwyż).
Właściwość pozwalającą ustawić rozmiar pamięci podręcznej (od 1 wzwyż).
Metodę zwracającą wiadomość o stanie aplikacji tj.:
liczbie wątków,
stanie pamięci podręcznej (liczba zajętych wpisów, liczba wolnych wpisów),
procencie chybień (błędów) pamięci podręcznej.
Należy wykorzystać jmc lub jconsole by podłączyć się do aplikacji i skorzystać z utworzonego ziarenka w celu monitorowania i zmiany przebiegu działania aplikacji.