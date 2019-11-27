import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {StatisticComponent} from './statistic.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

describe('StatisticComponent', () => {
  let component: StatisticComponent;
  let fixture: ComponentFixture<StatisticComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StatisticComponent ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatisticComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Testing component initialization.
   */
  it('Should be initialized', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h4').textContent).toContain('Component title');
    expect(compiled.querySelector('h5').textContent).toContain('n/a');
  });

  /**
   * Testing that the value updates itself.
   */
  it('Value should be updated', async () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h5').textContent).toContain('n/a');
    component.updateDisplayedValue('1000');
    fixture.detectChanges();
    expect(compiled.querySelector('h5').textContent).toContain('1000');
  });

});
