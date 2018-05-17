import {TestBed, async} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {StatisticComponent} from './statistic/statistic.component';
import {Server} from 'mock-socket';
import {Blockchain2graphService} from './blockchain2graph.service';

describe('AppComponent', () => {

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        StatisticComponent
      ],
      providers: [Blockchain2graphService]
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

  it('Should update statistic blocks', async(() => {
    const mockServer = new Server('ws://localhost:8080/status/websocket');
    const fixture = TestBed.createComponent(AppComponent);
    let message;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;

    // Checking that we have n/a.
    expect(compiled.querySelectorAll('h4')[0].textContent).toContain('Blocks in bitcoin core');
    expect(compiled.querySelectorAll('h4')[1].textContent).toContain('Blocks in neo4j');
    expect(compiled.querySelectorAll('h4')[2].textContent).toContain('Block import duration');
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    // Sending a message with nothing to see that we still display nothing.
    message = `
      {
        "blocksCountInBitcoinCore":-1,
        "blocksCountInNeo4j":-1,
          "currentBlockStatus":{
            "blockHeight":-1,
            "processStep":"NOTHING_TO_PROCESS",
            "processedAddresses":-1,
            "addressesCount":-1,
            "processedTransactions":-1,
            "transactionsCount":-1
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      }`;
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
      expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
      expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    // Changing the blocks count in bitcoin core.
    message = `
      {
        "blocksCountInBitcoinCore":2,
        "blocksCountInNeo4j":3,
          "currentBlockStatus":{
            "blockHeight":-1,
            "processStep":"NOTHING_TO_PROCESS",
            "processedAddresses":-1,
            "addressesCount":-1,
            "processedTransactions":-1,
            "transactionsCount":-1
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      }`;
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    // Changing the average block import duration.
    message = `
      {
        "blocksCountInBitcoinCore":2,
        "blocksCountInNeo4j":3,
          "currentBlockStatus":{
            "blockHeight":-1,
            "processStep":"NOTHING_TO_PROCESS",
            "processedAddresses":-1,
            "addressesCount":-1,
            "processedTransactions":-1,
            "transactionsCount":-1
          },
        "averageBlockProcessDuration":2.3,
        "lastErrorMessage":"n/a"
      }`;
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('2.30 s');

    // We reset everything.
    message = `
      {
        "blocksCountInBitcoinCore":-1,
        "blocksCountInNeo4j":-1,
          "currentBlockStatus":{
            "blockHeight":-1,
            "processStep":"NOTHING_TO_PROCESS",
            "processedAddresses":-1,
            "addressesCount":-1,
            "processedTransactions":-1,
            "transactionsCount":-1
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      }`;
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');



      /*// New message (blocksInBitcoinCore) from blockchain2graph server.
      const blocksInBitcoinCore1 = {
        'messageType': Blockchain2graphMessageType.BLOCKS_IN_BITCOIN_CORE,
        'messageValue': 2000
      };
      mockServer.send(JSON.stringify(blocksInBitcoinCore1));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2 000');
      expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
      expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

      // New message (blocksInNeo4j) from blockchain2graph server.
      const blocksInNeo4j1 = {
        'messageType': Blockchain2graphMessageType.BLOCKS_IN_NEO4J,
        'messageValue': 3000
      };
      mockServer.send(JSON.stringify(blocksInNeo4j1));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2 000');
      expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3 000');
      expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

      // New message (blockImportDuration) from blockchain2graph server.
      const blockImportDuration1 = {
        'messageType': Blockchain2graphMessageType.BLOCK_IMPORT_DURATION,
        'messageValue': 11114.123
      };
      mockServer.send(JSON.stringify(blockImportDuration1));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2 000');
      expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3 000');
      expect(compiled.querySelectorAll('h5')[2].textContent).toContain('11114.12 s');*/
    })
  );

})
;
