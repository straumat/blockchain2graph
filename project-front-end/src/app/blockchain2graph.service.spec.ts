import { TestBed, inject } from '@angular/core/testing';

import { Blockchain2graphService } from './blockchain2graph.service';

describe('Blockchain2graphService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Blockchain2graphService]
    });
  });

  it('should be created', inject([Blockchain2graphService], (service: Blockchain2graphService) => {
    expect(service).toBeTruthy();
  }));
});
