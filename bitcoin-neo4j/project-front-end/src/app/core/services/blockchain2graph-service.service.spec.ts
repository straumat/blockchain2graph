import { TestBed } from '@angular/core/testing';

import { Blockchain2graphService } from './blockchain2graph-service.service';

describe('Blockchain2graphServiceService', () => {
  let service: Blockchain2graphService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Blockchain2graphService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
