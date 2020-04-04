import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ArticleDTO } from '../model/ArticleDTO';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  private serverUrl: String = "http://localhost:8080";
  private file: File;
  private label: String;
  private isFileSelected: boolean;
  private numberOfArticles: number;
  private noTeaching: number;
  private noTesting: number;
  private articles: Array<ArticleDTO>;

  constructor(private http: HttpClient, private spinnerService: NgxSpinnerService) {
    this.isFileSelected = false;
    this.label = 'Wybierz plik';
  }

  ngOnInit() { }

  public handleFileInput(files) {
    if (files[0] !== undefined) {
      this.spinnerService.show();
      this.file = files[0];
      this.label = this.file.name;
      let fileReader = new FileReader();
      fileReader.onload = (e) => {
        this.uploadFile(fileReader.result.toString());
      }
      fileReader.readAsText(this.file);
      this.isFileSelected = true;
    }
  }

  public uploadFile(articles: String) {
    this.http.post<Array<ArticleDTO>>(this.serverUrl + '/applyArticles', articles).toPromise().then(response => {
      this.numberOfArticles = response.length;
      this.noTeaching = Math.round(this.numberOfArticles * 0.4);
      this.noTesting = this.numberOfArticles - this.noTeaching;
      this.spinnerService.hide();
      this.articles = response;
    });
  }

  public selectRatio(event) {
    this.noTeaching = Math.round(this.numberOfArticles * Number(event));
    this.noTesting = this.numberOfArticles - this.noTeaching;
  }
}
