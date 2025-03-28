import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import Button from '../src/components/Button';

describe('Button Component', () => {
  it('renders correctly with default props', () => {
    const { getByText } = render(<Button title="Test Button" />);
    expect(getByText('Test Button')).toBeTruthy();
  });

  it('calls onPress function when pressed', () => {
    const onPressMock = jest.fn();
    const { getByText } = render(
      <Button title="Pressable Button" onPress={onPressMock} />
    );

    fireEvent.press(getByText('Pressable Button'));
    expect(onPressMock).toHaveBeenCalledTimes(1);
  });

  it('applies correct style for primary variant', () => {
    const { getByText } = render(<Button title="Primary Button" variant="primary" />);
    const buttonText = getByText('Primary Button');
    
    // Verificar que o texto do botão primário tem estilo correto (cor branca)
    expect(buttonText.props.style).toEqual(
      expect.arrayContaining([
        expect.objectContaining({ color: 'white' })
      ])
    );
  });

  it('applies correct style for outline variant', () => {
    const { getByText } = render(<Button title="Outline Button" variant="outline" />);
    const buttonText = getByText('Outline Button');
    
    // Verificar que o texto do botão outline tem o estilo correto
    expect(buttonText.props.style).toEqual(
      expect.arrayContaining([
        expect.objectContaining({ color: expect.any(String) })
      ])
    );
  });
});
