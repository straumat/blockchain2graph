import {TestBed, async} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {StatisticComponent} from './statistic/statistic.component';

describe('AppComponent', () => {

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        StatisticComponent
      ],
    }).compileComponents();
  }));

  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

  it(`should have as title 'Blockchain2graph'`, async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.title).toEqual('Blockchain2graph');
  }));

  it('should render title in a h1 tag', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain('Blockchain2graph');
  }));

  it('Should have statistic blocks', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    // All icons are set.
    expect(compiled.querySelector('.fa.fa-question')).toBeNull();

    // Number of blocks in bitcoin core.
    expect(compiled.querySelector('.fa.fa-server')).not.toBeNull();
    expect(compiled.querySelectorAll('h4')[0].textContent).toContain('Blocks in bitcoin core');
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');

    // Number of blocks in neo4j.
    expect(compiled.querySelector('.fa.fa-database')).not.toBeNull();
    expect(compiled.querySelectorAll('h4')[1].textContent).toContain('Blocks in neo4j');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');

    // Average block import duration.
    expect(compiled.querySelector('.fa.fa-hourglass-half')).not.toBeNull();
    expect(compiled.querySelectorAll('h4')[2].textContent).toContain('Block import duration');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');
  }));

});
