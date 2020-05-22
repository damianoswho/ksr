export class ArticleDTO {
    date: String;
    topics: Array<String>;
    places: Array<String>;
    clasifiedPlace: String;
    people: Array<String>;
    orgs: Array<String>;
    exchanges: Array<String>;
    companies: Array<String>;
    title: String;
    body: String;
    stemmedBody: String;
    attributes: Array<Number>;
}