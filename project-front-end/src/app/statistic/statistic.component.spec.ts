import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {StatisticComponent} from './statistic.component';

describe('StatisticComponent', () => {
  let component: StatisticComponent;
  let fixture: ComponentFixture<StatisticComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StatisticComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatisticComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('Should be initialized', () => {
    const scFixture = TestBed.createComponent(StatisticComponent);
    scFixture.detectChanges();
    const compiled = scFixture.debugElement.nativeElement;
    expect(compiled.querySelector('.fa.fa-question')).not.toBeNull();
    expect(compiled.querySelector('h4').textContent).toContain('Component title');
    expect(compiled.querySelector('h5').textContent).toContain('n/a');
  });

});
