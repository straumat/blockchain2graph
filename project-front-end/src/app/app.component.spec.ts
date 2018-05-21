import {TestBed, async} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {StatisticComponent} from './statistic/statistic.component';
import {Server} from 'mock-socket';
import {Blockchain2graphService} from './blockchain2graph.service';
import {CurrentBlockStatusComponent} from './current-block-status/current-block-status.component';


describe('AppComponent', () => {

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        StatisticComponent,
        CurrentBlockStatusComponent
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

  it('Should update blocks', async(() => {
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
    message = {
        "blocksCountInBitcoinCore":2000,
        "blocksCountInNeo4j":1000,
          "currentBlockStatus":{
            "blockHeight":-1,
            "processStep":"NOTHING_TO_PROCESS",
            "processedAddresses":-1,
            "addressesCount":-1,
            "processedTransactions":-1,
            "transactionsCount":-1
          },
        "averageBlockProcessDuration":17.4545,
        "lastErrorMessage":"n/a"
      };

    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2 000');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('1 000');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('17.45 s');


    // Sending a message with nothing to see that we still display nothing.
    message = {
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
      };
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
      expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
      expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    // Changing the blocks count in bitcoin core.
    message = {
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
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    // Changing the average block import duration.
    message = {
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
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('2.30 s');

    // We reset everything.
    message = {
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
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    mockServer.close();
    })
  );

  it('Should update statistic blocks', async(() => {
      const mockServer = new Server('ws://localhost:8080/status/websocket');
      const fixture = TestBed.createComponent(AppComponent);
      let message;
      fixture.detectChanges();
      const compiled = fixture.debugElement.nativeElement;

      // Checking that we have nothing.
      expect(compiled.querySelectorAll('h4')[3].textContent).toContain('No block to process');
      expect(compiled.querySelector('div.progress-bar')).toBeNull();

      // Sending a message with nothing to see.
      message = {
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
      };
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h4')[3].textContent).toContain('No block to process');
      expect(compiled.querySelector('div.progress-bar')).toBeNull();

      // Sending a message of a new block to process (NEW_BLOCK_TO_PROCESS).
      message = {
        "blocksCountInBitcoinCore":-1,
        "blocksCountInNeo4j":-1,
          "currentBlockStatus":{
            "blockHeight":10,
            "processStep":"NEW_BLOCK_TO_PROCESS",
            "loadedTransactions":-1,
            "processedAddresses":-1,
            "addressesCount":-1,
            "processedTransactions":-1,
            "transactionsCount":-1
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      };
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
      expect(compiled.querySelector('div.progress-bar')).toBeNull();

      // Sending a message of loading block transactions (LOADING_TRANSACTIONS_FROM_BITCOIN_CORE).
      message = {
        "blocksCountInBitcoinCore":-1,
        "blocksCountInNeo4j":-1,
          "currentBlockStatus":{
            "blockHeight":10,
            "processStep":"LOADING_TRANSACTIONS_FROM_BITCOIN_CORE",
            "loadedTransactions":200,
            "processedAddresses":-1,
            "addressesCount":-1,
            "processedTransactions":-1,
            "transactionsCount":1000
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      };
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
      expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Loading transactions from bitcoin core...');
      expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
      expect(compiled.querySelector('div.progress-bar').textContent).toContain('20 %');

    // Sending a message of processing addresses (PROCESSING_ADDRESSES).
    message = {
        "blocksCountInBitcoinCore":-1,
        "blocksCountInNeo4j":-1,
          "currentBlockStatus":{
            "blockHeight":10,
            "processStep":"PROCESSING_ADDRESSES",
            "loadedTransactions":200,
            "processedAddresses":25,
            "addressesCount":100,
            "processedTransactions":-1,
            "transactionsCount":1000
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Processing addresses...');
    expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
    expect(compiled.querySelector('div.progress-bar').textContent).toContain('25 %');

    // Sending a message of processing transactions (PROCESSING_TRANSACTIONS).
    message = {
        "blocksCountInBitcoinCore":-1,
        "blocksCountInNeo4j":-1,
          "currentBlockStatus":{
            "blockHeight":10,
            "processStep":"PROCESSING_TRANSACTIONS",
            "loadedTransactions":200,
            "processedAddresses":25,
            "addressesCount":100,
            "processedTransactions":351,
            "transactionsCount":1000
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Processing transactions...');
    expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
    expect(compiled.querySelector('div.progress-bar').textContent).toContain('35 %');

    // Sending a message of saving block (SAVING_BLOCK).
    message = {
        "blocksCountInBitcoinCore":-1,
        "blocksCountInNeo4j":-1,
          "currentBlockStatus":{
            "blockHeight":10,
            "processStep":"SAVING_BLOCK",
            "loadedTransactions":200,
            "processedAddresses":25,
            "addressesCount":100,
            "processedTransactions":351,
            "transactionsCount":1000
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Saving block...');
    expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
    expect(compiled.querySelector('div.progress-bar').textContent).toContain('100 %');

    // Sending a message of block saved (BLOCK_SAVED).
    message = {
        "blocksCountInBitcoinCore":-1,
        "blocksCountInNeo4j":-1,
          "currentBlockStatus":{
            "blockHeight":10,
            "processStep":"BLOCK_SAVED",
            "loadedTransactions":200,
            "processedAddresses":25,
            "addressesCount":100,
            "processedTransactions":351,
            "transactionsCount":1000
          },
        "averageBlockProcessDuration":-1.0,
        "lastErrorMessage":"n/a"
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Block saved');
    expect(compiled.querySelector('div.progress-bar')).not.toBeNull();

    mockServer.close();
  }));


});
