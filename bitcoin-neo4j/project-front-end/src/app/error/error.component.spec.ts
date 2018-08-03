import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ErrorComponent } from './error.component';
import {Blockchain2graphService} from '../blockchain2graph.service';

/**
 * Error component test.
 */
describe('ErrorComponent', () => {
  let component: ErrorComponent;
  let fixture: ComponentFixture<ErrorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ErrorComponent ],
      providers: [Blockchain2graphService]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Testing creation.
   */
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /**
   * Testing that the component can get visible/not visible and display message.
   */
  it('should be visible / not visible depending on errors existing', () => {
    const compiled = fixture.debugElement.nativeElement;
    // Not visible by default.
    fixture.detectChanges();
    expect(compiled.querySelector('div.card-body')).toBeNull();
    expect(compiled.querySelector('div.card-body')).toBeNull();
    // Visible thanks to viewError.
    component.viewError = true;
    fixture.detectChanges();
    expect(compiled.querySelector('div.card-body')).not.toBeNull();
    expect(compiled.querySelector('div.card-body')).not.toBeNull();
    // Check that it can be disabled.
    component.viewError = false;
    fixture.detectChanges();
    expect(compiled.querySelector('div.card-body')).toBeNull();
    expect(compiled.querySelector('div.card-body')).toBeNull();
  });

});
