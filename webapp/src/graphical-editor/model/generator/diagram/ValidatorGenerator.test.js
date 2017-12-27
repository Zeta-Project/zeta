import ValidatorGenerator from './ValidatorGenerator';

describe('validator.inputMatrix', () => {
    test('metaModel is empty', () => {
        const validator = new ValidatorGenerator({});
        expect(validator.inputMatrix).toEqual({});
    });

    test('metaModel has class and class has input', () => {
        const validator = new ValidatorGenerator({
            classes: [
                {
                    name: 'Class',
                    inputs: [
                        {
                            type: 'Reference',
                            lowerBound: 2,
                            upperBound: 3,
                        }
                    ],
                }
            ],
        });
        expect(validator.inputMatrix).toEqual({
            'Class': {
                'Reference': {
                    'upperBound': 3,
                    'lowerBound': 2,
                }
            },
        });
    });
});

describe('validator.outputMatrix', () => {
    test('metaModel is empty', () => {
        const validator = new ValidatorGenerator({});
        expect(validator.outputMatrix).toEqual({});
    });

    test('metaModel has class and class has input', () => {
        const validator = new ValidatorGenerator({
            classes: [
                {
                    name: 'Class',
                    outputs: [
                        {
                            type: 'Reference',
                            lowerBound: 5,
                            upperBound: 7,
                        }
                    ],
                }
            ],
        });
        expect(validator.outputMatrix).toEqual({
            'Class': {
                'Reference': {
                    'upperBound': 7,
                    'lowerBound': 5,
                }
            },
        });
    });
});