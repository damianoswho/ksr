import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ArticleDTO } from '../model/ArticleDTO';
import { NgxSpinnerService } from 'ngx-spinner';
import { Result } from '../model/Result';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  private serverUrl: String = environment.serverUrl;
  private file: File;
  public label: String;
  public step: Number = 1;
  public isFileSelected: boolean;
  public numberOfArticles: number;
  public noTeaching: number;
  public noTesting: number;
  public metric = 'EUKLIDESOWA';
  public numbersForKnn = Array<Number>(15).fill(1).map((x, i) => i + 1);;
  public kForKnn = 1;
  public attributesToProcess: Array<Boolean> = new Array<Boolean>();
  public result: Result;
  public includeDisproportion = false;
  public attributesDescriptions = [
    'C1 - Liczba unikatowych słów ważnych. - Ilość unikatowych słów W.',
    'C2 - Stosunek słów ważnych do nieważnych. - Stosunek W do liczby słów oryginalnego artykułu A (oryginalnego czyli przed poddaniem stemizacji oraz usunięciu słów ze stop listy).',
    'C3 - Średnia liczba powtórzeń słów ważnych - W podzielona przez C1. ',
    'C4 - Liczba zdań w artykule - Liczba wystąpień kropki (znaku interpunkcyjnego). ',
    'C5 - Średnia liczba słów ważnych w zdaniu - W podzielona przez C4. ',
    'C6 - Liczba akapitów - Liczba akapitów w artykule.',
    'C7 - Średnia liczba zdań w akapicie - C4 podzielona przez C6. ',
    'C8 - Czy występują cytaty - Wartość 0-1 stwierdzająca, czy w artykule wystąpił znak ".',
    'C9 - Liczba wystąpień unikatowych słów kluczowych w artykule - słowa wybrane w trakcie nauczania programu, zależne od etykiety, których algorytm wyboru zostanie podany w dalszej części sprawozdania. ',
    'C10 - Średnia liczba powtórzeń słów kluczowych - Liczba wystąpień słów kluczowych podzielona przez C9. ',
    'C11 - Średnia długość słowa kluczowego - Suma znaków wszystkich słów kluczowych podzielona przez C9. ',
    'C12 - Maksymalna liczba słów ważnych dzielących dwa słowa kluczowe.',
    'C13 - Czy słowa kluczowe występują w więcej niż jednym akapicie.'
  ];

  constructor(private http: HttpClient, private spinnerService: NgxSpinnerService) {
    this.isFileSelected = false;
    this.label = 'Wybierz plik';
  }

  ngOnInit() {
    this.attributesToProcess = Array<Boolean>(this.attributesDescriptions.length).fill(true);
  }

  public handleFileInput(files: FileList) {
    this.spinnerService.show();
    let fileNames: Array<String> = new Array();
    const fileList: Array<String> = new Array();
    for (let i = 0; i < files.length; i++) {
      fileNames.push(files.item(i).name);
      let fileReader = new FileReader();
      fileReader.onload = (e) => {
        fileList.push(fileReader.result + '');
        if (fileList.length == files.length) {
          this.uploadFile(fileList);
        }
      }
      fileReader.readAsText(files[i]);
    }
    this.label = fileNames.join(', ');
  }

  public uploadFile(files: Array<String>) {
    this.http.post<number>(this.serverUrl + '/applyArticles', files).toPromise().then(response => {
      this.numberOfArticles = response;
      this.selectRatio('0.4');
      this.spinnerService.hide();
      this.step = 2;
    });
  }

  public selectRatio(event) {
    this.noTeaching = Math.round(this.numberOfArticles * Number(event));
    this.noTesting = this.numberOfArticles - this.noTeaching;
  }

  public selectMetric(event) {
    this.metric = event;
  }

  public selectKForKnn(event) {
    this.kForKnn = event;
  }

  public selectAttribute(event) {
    this.attributesToProcess[event] = !this.attributesToProcess[event];
  }

  public changeNumberOfTeaching(){
    if (this.noTeaching <= this.numberOfArticles && this.noTeaching > 0) {
      this.noTesting = this.numberOfArticles - this.noTeaching;
    } else {
      this.noTeaching = this.numberOfArticles - this.noTesting;
    }
  }


  public teach() {
    this.spinnerService.show();
    this.http.post<Array<ArticleDTO>>(this.serverUrl + '/teach', {}, { params: { 'noTeachingItems': this.noTeaching.toString(), 'includeDisproportion': this.includeDisproportion + '' } }).toPromise().then(response => {
      this.step = this.step.valueOf() + 1;
      this.result = undefined;
      this.spinnerService.hide();
    }).catch(error => {
      console.error(error);
    });
  }

  public classify() {
    this.spinnerService.show();
    this.http.post<Result>(this.serverUrl + '/classify', {}, { params: { 'metric': this.metric, 'k': this.kForKnn.toString(), 'attributesToProcess': this.attributesToProcess.toString() } }).toPromise().then(response => {
      this.result = response;
      this.step = this.step.valueOf() + 1;
      this.spinnerService.hide();
    }).catch(error => {
      console.error(error);
    });
  }

  public setNewWorkingSet(files) {
    if (files[0] !== undefined) {
      this.spinnerService.show();
      this.file = files[0];
      this.label = this.file.name;
      let fileReader = new FileReader();
      fileReader.onload = (e) => {
        this.http.post<number>(this.serverUrl + '/setNewWorkingSet', fileReader.result.toString()).toPromise().then(response => {
          this.step = this.step.valueOf() + 1;
          this.spinnerService.hide();
        });
      }
      fileReader.readAsText(this.file);
      this.isFileSelected = true;
    }
  }

  public selectDisproportion(event) {
    this.includeDisproportion = event;
  }
}
