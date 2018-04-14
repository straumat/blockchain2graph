import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {StatisticComponent} from './statistic.component';
import {Blockchain2graphService} from '../blockchain2graph.service';

describe('StatisticComponent', () => {
  let component: StatisticComponent;
  let fixture: ComponentFixture<StatisticComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StatisticComponent],
      providers: [Blockchain2graphService]
    }).compileComponents();
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
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('.fa.fa-question')).not.toBeNull();
    expect(compiled.querySelector('h4').textContent).toContain('Component title');
    expect(compiled.querySelector('h5').textContent).toContain('n/a');
  });

  it('Value should be updated', async () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h5').textContent).toContain('n/a');
    component.updateValue('1000');
    fixture.detectChanges();
    expect(compiled.querySelector('h5').textContent).toContain('1000');
  });

});
