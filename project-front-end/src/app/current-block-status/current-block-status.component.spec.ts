import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentBlockStatusComponent } from './current-block-status.component';

describe('CurrentBlockStatusComponent', () => {
  let component: CurrentBlockStatusComponent;
  let fixture: ComponentFixture<CurrentBlockStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CurrentBlockStatusComponent ]
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
});
