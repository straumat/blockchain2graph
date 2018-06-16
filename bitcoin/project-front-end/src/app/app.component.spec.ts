import {TestBed, async} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {StatisticComponent} from './statistic/statistic.component';
import {Server} from 'mock-socket';
import {Blockchain2graphService} from './blockchain2graph.service';
import {CurrentBlockStatusComponent} from './current-block-status/current-block-status.component';
import {ErrorComponent} from './error/error.component';

/**
 * Application test.
 */
describe('AppComponent', () => {

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        StatisticComponent,
        CurrentBlockStatusComponent,
        ErrorComponent
      ],
      providers: [Blockchain2graphService]
    }).compileComponents();
  }));

  /**
   * Testing application creation.
   */
  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

  /**
   * Testing that the title really appears..
   */
  it(`should have as title 'Blockchain2graph'`, async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.title).toEqual('Blockchain2graph - bitcoin');
  }));

  /**
   * Testing that the statics blocks are created with the correct parameters.
   */
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

  /**
   * Testing that server message update statistics blocks.
   */
  it('Should update statistics blocks', async(() => {
    const webSocketURL = 'ws://' + location.host + '/' + Blockchain2graphService.webSocketPath;
    const mockServer = new Server(webSocketURL);
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

    // Updating values.
    message = {
        'blockCountInBitcoinCore': 2000,
        'blockCountInNeo4j': 1000,
          'currentBlockStatus': {
            'blockHeight': -1,
            'processStep': 'NOTHING_TO_PROCESS',
            'processedAddresses': -1,
            'addressCount': -1,
            'processedTransactions': -1,
            'transactionCount': -1
          },
        'lastBlockProcessDuration': 17454,
        'lastErrorMessage': 'n/a'
      };

    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2 000');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('1 000');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('17.45 s');

    // Sending a message with nothing to see that we still display nothing.
    message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': -1,
            'processStep': 'NOTHING_TO_PROCESS',
            'processedAddresses': -1,
            'addressCount': -1,
            'processedTransactions': -1,
            'transactionCount': -1
          },
        'lastBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
      expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
      expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    // Changing the blocks count in bitcoin core.
    message = {
        'blockCountInBitcoinCore': 2,
        'blockCountInNeo4j': 3,
          'currentBlockStatus': {
            'blockHeight': -1,
            'processStep': 'NOTHING_TO_PROCESS',
            'processedAddresses': -1,
            'addressCount': -1,
            'processedTransactions': -1,
            'transactionCount': -1
          },
        'lastBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    // Changing the average block import duration.
    message = {
        'blockCountInBitcoinCore': 2,
        'blockCountInNeo4j': 3,
          'currentBlockStatus': {
            'blockHeight': -1,
            'processStep': 'NOTHING_TO_PROCESS',
            'processedAddresses': -1,
            'addressCount': -1,
            'processedTransactions': -1,
            'transactionCount': -1
          },
        'lastBlockProcessDuration': 2300,
        'lastErrorMessage': 'n/a'
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('2');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('3');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('2.30 s');

    // We reset everything.
    message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': -1,
            'processStep': 'NOTHING_TO_PROCESS',
            'processedAddresses': -1,
            'addressCount': -1,
            'processedTransactions': -1,
            'transactionCount': -1
          },
        'lastBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h5')[0].textContent).toContain('n/a');
    expect(compiled.querySelectorAll('h5')[1].textContent).toContain('n/a');
    expect(compiled.querySelectorAll('h5')[2].textContent).toContain('n/a');

    mockServer.close();
    })
  );

  /**
   * Testing the statistic blocks update from the server.
   */
  it('Should update statistic blocks', async(() => {
    const webSocketURL = 'ws://' + location.host + '/' + Blockchain2graphService.webSocketPath;
    const mockServer = new Server(webSocketURL);
      const fixture = TestBed.createComponent(AppComponent);
      let message;
      fixture.detectChanges();
      const compiled = fixture.debugElement.nativeElement;

      // Checking that we have nothing.
      expect(compiled.querySelectorAll('h4')[3].textContent).toContain('No block to process');
      expect(compiled.querySelector('div.progress-bar')).toBeNull();

      // Sending a message with nothing to see.
      message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': -1,
            'processStep': 'NOTHING_TO_PROCESS',
            'processedAddresses': -1,
            'addressCount': -1,
            'processedTransactions': -1,
            'transactionCount': -1
          },
        'averageBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h4')[3].textContent).toContain('No block to process');
      expect(compiled.querySelector('div.progress-bar')).toBeNull();

      // Sending a message of a new block to process (NEW_BLOCK_TO_PROCESS).
      message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': 10,
            'processStep': 'NEW_BLOCK_TO_PROCESS',
            'loadedTransactions': -1,
            'processedAddresses': -1,
            'addressCount': -1,
            'processedTransactions': -1,
            'transactionCount': -1
          },
        'averageBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
      expect(compiled.querySelector('div.progress-bar')).not.toBeNull();

      // Sending a message of loading block transactions (LOADING_TRANSACTIONS_FROM_BITCOIN_CORE).
      message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': 10,
            'processStep': 'LOADING_TRANSACTIONS_FROM_BITCOIN_CORE',
            'loadedTransactions': 200,
            'processedAddresses': -1,
            'addressCount': -1,
            'processedTransactions': -1,
            'transactionCount': 1000
          },
        'averageBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
      mockServer.send(JSON.stringify(message));
      fixture.detectChanges();
      expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
      expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Loading transactions from bitcoin core...');
      expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
      expect(compiled.querySelector('div.progress-bar').textContent).toContain('20 %');

    // Sending a message of processing addresses (PROCESSING_ADDRESSES).
    message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': 10,
            'processStep': 'PROCESSING_ADDRESSES',
            'loadedTransactions': 200,
            'processedAddresses': 25,
            'addressCount': 100,
            'processedTransactions': -1,
            'transactionCount': 1000
          },
        'averageBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Processing addresses...');
    expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
    expect(compiled.querySelector('div.progress-bar').textContent).toContain('25 %');

    // Sending a message of processing transactions (PROCESSING_TRANSACTIONS).
    message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': 10,
            'processStep': 'PROCESSING_TRANSACTIONS',
            'loadedTransactions': 200,
            'processedAddresses': 25,
            'addressCount': 100,
            'processedTransactions': 351,
            'transactionCount': 1000
          },
        'averageBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Processing transactions...');
    expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
    expect(compiled.querySelector('div.progress-bar').textContent).toContain('35 %');

    // Sending a message of saving block (SAVING_BLOCK).
    message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': 10,
            'processStep': 'SAVING_BLOCK',
            'loadedTransactions': 200,
            'processedAddresses': 25,
            'addressCount': 100,
            'processedTransactions': 351,
            'transactionCount': 1000
          },
        'averageBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Saving block...');
    expect(compiled.querySelector('div.progress-bar')).not.toBeNull();
    expect(compiled.querySelector('div.progress-bar').textContent).toContain('100 %');

    // Sending a message of block saved (BLOCK_SAVED).
    message = {
        'blockCountInBitcoinCore': -1,
        'blockCountInNeo4j': -1,
          'currentBlockStatus': {
            'blockHeight': 10,
            'processStep': 'BLOCK_SAVED',
            'loadedTransactions': 200,
            'processedAddresses': 25,
            'addressCount': 100,
            'processedTransactions': 351,
            'transactionCount': 1000
          },
        'averageBlockProcessDuration': -1.0,
        'lastErrorMessage': 'n/a'
      };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelectorAll('h4')[3].textContent).toContain('Block 00000010');
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('Block saved');
    expect(compiled.querySelector('div.progress-bar')).not.toBeNull();

    mockServer.close();
  }));

  /**
   * Testing the display of errors.
   */
  it('Should display errors', async(() => {
    const webSocketURL = 'ws://' + location.host + '/' + Blockchain2graphService.webSocketPath;
    const mockServer = new Server(webSocketURL);
    const fixture = TestBed.createComponent(AppComponent);
    let message;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;

    // Checking that we have no error by default.
    expect(compiled.querySelector('div.card-header.bg-danger')).toBeNull();
    expect(compiled.querySelector('div.body.bg-danger')).toBeNull();

    // Sending a message with nothing to see and check that there is no error.
    message = {
      'blockCountInBitcoinCore': -1,
      'blockCountInNeo4j': -1,
      'currentBlockStatus': {
        'blockHeight': -1,
        'processStep': 'NOTHING_TO_PROCESS',
        'processedAddresses': -1,
        'addressCount': -1,
        'processedTransactions': -1,
        'transactionCount': -1
      },
      'lastBlockProcessDuration': -1.0,
      'lastErrorMessage': 'n/a'
    };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelector('div.bg-danger')).toBeNull();

    // Sending an error message.
    message = {
      'blockCountInBitcoinCore': -1,
      'blockCountInNeo4j': -1,
      'currentBlockStatus': {
        'blockHeight': -1,
        'processStep': 'NOTHING_TO_PROCESS',
        'processedAddresses': -1,
        'addressCount': -1,
        'processedTransactions': -1,
        'transactionCount': -1
      },
      'lastBlockProcessDuration': -1.0,
      'lastErrorMessage': 'This is an error message'
    };
    mockServer.send(JSON.stringify(message));
    fixture.detectChanges();
    expect(compiled.querySelector('div.bg-danger')[0]).not.toBeNull();
    expect(compiled.querySelector('div.bg-danger')[1]).not.toBeNull();
    expect(compiled.querySelectorAll('h5')[3].textContent).toContain('This is an error message');
  }));

});
