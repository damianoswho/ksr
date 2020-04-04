<h3>Komputerowe Systemy Rozpoznawania</h3>

<h3>Damian Olczyk, Tomasz Majchrzak, Michał Kabat</h3>

<h4>Projekt 1 – ekstrakcja cech, miary podobieństwa, klasyfikacja</h4>
<p>Na ocenę 4.0:</p>
<ul>
  <li>Stworzyć aplikację do klasyfikacji zbioru tekstów metodą k-NN zawierającą:</li>
  <ul>
    <li>Moduł ekstrakcji cech operujący na dostarczonym zbiorze tekstów. Ekstrahowane cechy
      muszą być tak dobrane, aby były niezależne od liczby obiektów/tekstów w bazie
      i reprezentowały pojedyncze obiekty w sposób niezmienny (a nie względem pozostałej
      części zbioru). Zbiór tekstów do pobrania wraz z opisem:
      http://archive.ics.uci.edu/ml/datasets/Reuters-21578+Text+Categorization+Collection
    </li>
    <li>Moduł klasyfikatora k-NN operujący na zbiorze wektorów cech reprezentujących teksty.
Klasyfikator przyjmuje następujące parametry:
<ul>
  <li>wartość k,</li>
  <li>proporcje podziału zbioru wektorów na zbiór uczący i testowy (np. 60/40, 30/70,
50/50), w sposób deterministyczny, tzn. dla kilku kolejnych doświadczeń dany zbiór
uczący/testowy zawiera dokładnie te same elementy (czyli obiekty/teksty),</li>
  <li>zbiór cech, na podstawie których dokonuje się dana klasyfikacja (nie każda
klasyfikacja musi brać pod uwagę wszystkie cechy wyekstrahowane z tekstów)</li>
  <li>metrykę lub miarę podobieństwa zastosowaną w metodzie k-NN (patrz niżej).</li>
      </ul>
</ul>
<li>Należy wykonać zadanie klasyfikacji tekstów, które w kategorii places
posiadają etykiety: west-germany, usa, france, uk, canada, japan i są to
ich jedyne etykiety w tej kategorii</li>
  <li>W procesie klasyfikacji należy rozważyć następujące metryki i miary:</li>
<ul>
  <li>(M1) Metryka euklidesowa</li>
<li>(M2) Metryka uliczna</li>
<li>(M3) Metryka Czebyszewa</li>
<li>Wybrana miara podobieństwa zbiorów/wektorów (1) – patrz wykład</li>
<li>Wybrana miara podobieństwa zbiorów/wektorów (2) – patrz wykład</li>
</ul>
  <li>W wynikach klasyfikacji należy każdorazowo podać wartości następujących miary jakości:</li>
<ul>
  <li>Accuracy</li>
<li>Precision</li>
<li>Recall</li>
  </ul>
<li>Porównać wyniki klasyfikacji metody k-NN dla 10 różnych wartości parametru k (wyznaczyć
zależność Accuracy od k, przy stałych wartościach innych parametrów).</li>
<li>Przy wybranej stałej wartości k wyznaczyć zależność Accuracy od pięciu wartości proporcji
podziału zbioru (przy pozostałych parametrach stałych).</li>
<li>Wyznaczyć zależność Accuracy od wyboru metryki/miary (przy pozostałych parametrach
stałych).</li>
<li>Na podstawie dowolnego wyboru 4-ch podzbiorów cech wskazać, które cechy potencjalnie
mają najmniejszy, a które największy wpływ na wyniki klasyfikacji, zwłaszcza na Accuracy
(przy innych wartościach stałych). </li>
  </ul>

<p>Dodatkowo na ocenę 5.0:</p>
<ul>
<li>Opracować własną miarę podobieństwa i/lub metrykę.</li>
<li>Porównać wyniki klasyfikacji metody k-NN dla literaturowej i własnej miary
podobieństwa/metryki oraz dla różnych wartości parametru k (opracowane miary/metryki
powinny poprawiać uzyskiwane wyniki klasyfikacji).</li>
<li>Wykonać klasyfikację tekstów w ramach innej kategorii niż opisana powyżej, z minimum
dwiema etykietami.</li>
  </ul>
  
<p>Sprawozdania należy opracować zgodnie z podanym szablonem.</p>
