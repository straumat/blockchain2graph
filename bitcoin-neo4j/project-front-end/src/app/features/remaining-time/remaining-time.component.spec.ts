import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RemainingTimeComponent} from './remaining-time.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

describe('RemainingTimeComponent', () => {
  let component: RemainingTimeComponent;
  let fixture: ComponentFixture<RemainingTimeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RemainingTimeComponent ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RemainingTimeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Testing initialization.
   */
  it('should be initialized', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h4').textContent).toContain('Remaining time not available for the moment');
    expect(compiled.querySelector('h5').textContent).toContain('Refreshed every minute');
  });

});
