import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CurrentBlockStatusComponent} from './current-block-status.component';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';

describe('CurrentBlockStatusComponent', () => {
  let component: CurrentBlockStatusComponent;
  let fixture: ComponentFixture<CurrentBlockStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CurrentBlockStatusComponent ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CurrentBlockStatusComponent);
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
    expect(compiled.querySelector('h4').textContent).toContain('No block to process');
    expect(compiled.querySelector('div.card-body')).toBeNull();
  });

  /**
   * Testing visibility.
   */
  it('should be visible / not visible depending on viewDetails', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('div.card-body')).toBeNull();
    component.viewDetails = true;
    fixture.detectChanges();
    expect(compiled.querySelector('div.card-body')).not.toBeNull();
  });

  /**
   * Testing block number display.
   */
  it('should display block number', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h4').textContent).toContain('No block to process');
    component.setBlockHeight(45);
    fixture.detectChanges();
    expect(compiled.querySelector('h4').textContent).toContain('Block 00000045');
  });

  /**
   * Testing process step display.
   */
  it('should display process description', () => {
    const compiled = fixture.debugElement.nativeElement;
    component.viewDetails = true;
    fixture.detectChanges();
    expect(compiled.querySelector('h5').textContent).toContain('No block to process');
    component.processStepDescription = 'toto';
    fixture.detectChanges();
    expect(compiled.querySelector('h5').textContent).toContain('toto');
  });

  /**
   * Testing progression display.
   */
  it('should display progression', () => {
    const compiled = fixture.debugElement.nativeElement;
    component.viewDetails = true;
    component.setProgression(33, 100);
    fixture.detectChanges();
    expect(compiled.querySelector('div.progress-bar').textContent).toContain('33 %');
    // Testing by 0 division
    component.setProgression(10, 0);
    fixture.detectChanges();
    expect(compiled.querySelector('div.progress-bar').textContent).toContain('0 %');
  });

});
